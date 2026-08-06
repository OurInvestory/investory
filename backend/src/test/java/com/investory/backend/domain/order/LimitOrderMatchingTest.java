package com.investory.backend.domain.order;

import com.investory.backend.domain.order.dto.OrderRequest;
import com.investory.backend.domain.order.dto.OrderResponse;
import com.investory.backend.domain.order.entity.Order;
import com.investory.backend.domain.order.service.LimitOrderMatcher;
import com.investory.backend.domain.order.service.OrderService;
import com.investory.backend.domain.stock.entity.Stock;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Phase 4-4 검증: 지정가 주문 매칭.
 * <p>
 * 시세 시뮬레이터는 테스트 프로파일에서 꺼 두고({@code simulator.enabled=false}),
 * 현재가를 테스트가 직접 조작한 뒤 매처를 호출한다.
 * 스케줄러가 배경에서 시세를 흔들면 "정확한 조건에서만 체결되는가"를 검증할 수 없기 때문이다.
 */
@DisplayName("지정가 주문 매칭 테스트")
class LimitOrderMatchingTest extends IntegrationTestSupport {

    @Autowired
    private OrderService orderService;

    @Autowired
    private LimitOrderMatcher limitOrderMatcher;

    @Test
    @DisplayName("지정가 매수 주문은 접수 시 체결되지 않고 PENDING 으로 남는다")
    void limitOrderStaysPendingOnPlacement() {
        User user = createUser("limiter", won(1_000_000));
        Stock stock = createStock("005930", "삼성전자", won(70_000));

        // 현재가(70,000)보다 낮은 65,000에 매수 주문
        OrderResponse.Detail result = orderService.createOrder(
                user.getLoginId(), buyLimit(stock.getCode(), 1, won(65_000)));

        assertThat(result.getStatus()).isEqualTo(Order.OrderStatus.PENDING.name());
        // 아직 체결 전이므로 잔액도 그대로여야 한다
        assertThat(reload(user).getCash()).isEqualByComparingTo(won(1_000_000));
        assertThat(cashHistoryRepository.countByUserId(user.getId())).isZero();
    }

    @Test
    @DisplayName("매수 지정가는 현재가가 지정가 이하로 내려왔을 때만 체결된다")
    void buyLimitFillsOnlyWhenPriceDropsToLimit() {
        User user = createUser("buyer", won(1_000_000));
        Stock stock = createStock("005930", "삼성전자", won(70_000));

        OrderResponse.Detail order = orderService.createOrder(
                user.getLoginId(), buyLimit(stock.getCode(), 2, won(65_000)));

        // when 1: 현재가가 아직 지정가보다 높으면 (66,000 > 65,000) 체결되지 않는다
        int filled = limitOrderMatcher.match(stock.getId(), won(66_000));

        assertThat(filled).isZero();
        assertThat(findOrder(order).getStatus()).isEqualTo(Order.OrderStatus.PENDING);

        // when 2: 지정가와 같아지면 (65,000 <= 65,000) 체결된다 — 경계값 포함 확인
        filled = limitOrderMatcher.match(stock.getId(), won(65_000));

        // then
        assertThat(filled).isEqualTo(1);
        Order executed = findOrder(order);
        assertThat(executed.getStatus()).isEqualTo(Order.OrderStatus.FILLED);
        // 체결가는 지정가가 아니라 실제 시장 현재가다
        assertThat(executed.getFilledPrice()).isEqualByComparingTo(won(65_000));
        assertThat(reload(user).getCash()).isEqualByComparingTo(won(870_000));
    }

    @Test
    @DisplayName("매도 지정가는 현재가가 지정가 이상으로 올라왔을 때만 체결된다")
    void sellLimitFillsOnlyWhenPriceRisesToLimit() {
        User user = createUser("seller", won(1_000_000));
        Stock stock = createStock("005930", "삼성전자", won(70_000));

        // 보유를 만든다 (시장가 매수 5주 = 350,000 → 잔액 650,000)
        orderService.createOrder(user.getLoginId(), buyMarket(stock.getCode(), 5));

        OrderResponse.Detail order = orderService.createOrder(
                user.getLoginId(), sellLimit(stock.getCode(), 2, won(75_000)));

        // when 1: 현재가가 지정가에 못 미치면 체결되지 않는다
        assertThat(limitOrderMatcher.match(stock.getId(), won(74_000))).isZero();
        assertThat(findOrder(order).getStatus()).isEqualTo(Order.OrderStatus.PENDING);

        // when 2: 지정가를 넘어서면 체결된다
        assertThat(limitOrderMatcher.match(stock.getId(), won(76_000))).isEqualTo(1);

        // then: 650,000 + (76,000 × 2) = 802,000
        assertThat(findOrder(order).getStatus()).isEqualTo(Order.OrderStatus.FILLED);
        assertThat(reload(user).getCash()).isEqualByComparingTo(won(802_000));
    }

    @Test
    @DisplayName("취소된 지정가 주문은 조건을 만족해도 체결되지 않는다")
    void cancelledLimitOrderIsNotMatched() {
        User user = createUser("canceller", won(1_000_000));
        Stock stock = createStock("005930", "삼성전자", won(70_000));

        OrderResponse.Detail order = orderService.createOrder(
                user.getLoginId(), buyLimit(stock.getCode(), 1, won(65_000)));
        orderService.cancelOrder(user.getLoginId(), order.getId(), "마음이 바뀜");

        // when: 체결 조건을 만족하는 시세가 와도
        int filled = limitOrderMatcher.match(stock.getId(), won(60_000));

        // then
        assertThat(filled).isZero();
        assertThat(findOrder(order).getStatus()).isEqualTo(Order.OrderStatus.CANCELLED);
        assertThat(reload(user).getCash()).isEqualByComparingTo(won(1_000_000));
    }

    @Test
    @DisplayName("접수 후 잔액이 모자라진 지정가 매수는 PENDING 이 아니라 REJECTED 로 종료된다")
    void insufficientCashAtFillTimeRejectsOrder() {
        // given: 딱 1주치 잔액
        User user = createUser("edge", won(70_000));
        Stock stock = createStock("005930", "삼성전자", won(70_000));

        // 지정가 매수 1주 접수 (아직 차감 안 됨)
        OrderResponse.Detail limitOrder = orderService.createOrder(
                user.getLoginId(), buyLimit(stock.getCode(), 1, won(70_000)));

        // 그 사이 시장가로 잔액을 다 써 버린다
        orderService.createOrder(user.getLoginId(), buyMarket(stock.getCode(), 1));
        assertThat(reload(user).getCash()).isEqualByComparingTo(won(0));

        // when: 체결 조건을 만족하는 시세가 와도 잔액이 없다
        int filled = limitOrderMatcher.match(stock.getId(), won(70_000));

        // then: 영원히 체결 안 되는 좀비 주문으로 남기지 않고 REJECTED 로 닫는다
        assertThat(filled).isZero();
        Order rejected = findOrder(limitOrder);
        assertThat(rejected.getStatus()).isEqualTo(Order.OrderStatus.REJECTED);
        assertThat(rejected.getCancelReason()).contains("부족");
    }

    @Test
    @DisplayName("시세 갱신 시 전일 종가 대비 변동률이 계산된다")
    void priceUpdateCalculatesChangeAgainstPreviousClose() {
        Stock stock = createStock("005930", "삼성전자", won(70_000));

        // 두 번 연속 갱신해도 previousClose 는 유지되어야 한다
        stock.updatePrice(BigDecimal.valueOf(70_700), 1000L);
        stock.updatePrice(BigDecimal.valueOf(71_400), 500L);

        // 71,400 vs 전일 종가 70,000 → +1,400 (+2.00%)
        assertThat(stock.getPreviousClose()).isEqualByComparingTo(won(70_000));
        assertThat(stock.getChangeAmount()).isEqualByComparingTo(BigDecimal.valueOf(1_400));
        assertThat(stock.getChangeRate()).isEqualByComparingTo(BigDecimal.valueOf(2.00));
        assertThat(stock.getVolume()).isEqualTo(1500L);
    }

    private Order findOrder(OrderResponse.Detail detail) {
        return orderRepository.findById(detail.getId()).orElseThrow();
    }

    private User reload(User user) {
        return userRepository.findById(user.getId()).orElseThrow();
    }

    private OrderRequest.Create buyMarket(String stockCode, int quantity) {
        return OrderRequest.Create.builder()
                .stockCode(stockCode).orderType(Order.OrderType.MARKET)
                .side(Order.OrderSide.BUY).quantity(quantity).build();
    }

    private OrderRequest.Create buyLimit(String stockCode, int quantity, BigDecimal price) {
        return OrderRequest.Create.builder()
                .stockCode(stockCode).orderType(Order.OrderType.LIMIT)
                .side(Order.OrderSide.BUY).quantity(quantity).price(price).build();
    }

    private OrderRequest.Create sellLimit(String stockCode, int quantity, BigDecimal price) {
        return OrderRequest.Create.builder()
                .stockCode(stockCode).orderType(Order.OrderType.LIMIT)
                .side(Order.OrderSide.SELL).quantity(quantity).price(price).build();
    }
}
