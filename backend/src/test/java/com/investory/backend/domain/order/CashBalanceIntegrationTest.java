package com.investory.backend.domain.order;

import com.investory.backend.domain.order.dto.OrderRequest;
import com.investory.backend.domain.order.dto.OrderResponse;
import com.investory.backend.domain.order.entity.Order;
import com.investory.backend.domain.order.service.OrderService;
import com.investory.backend.domain.portfolio.entity.Holding;
import com.investory.backend.domain.stock.entity.Stock;
import com.investory.backend.domain.user.entity.CashHistory;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.global.exception.BusinessException;
import com.investory.backend.global.exception.ErrorCode;
import com.investory.backend.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Phase 4-1 검증: 현금 잔액 도메인.
 */
@DisplayName("현금 잔액 통합 테스트")
class CashBalanceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName("잔액보다 비싼 매수 주문은 INSUFFICIENT_CASH 로 거부되고, 주문 자체가 저장되지 않는다")
    void rejectBuyWhenCashIsInsufficient() {
        // given: 잔액 10만원, 주가 7만원짜리 종목
        User user = createUser("poor", won(100_000));
        Stock stock = createStock("005930", "삼성전자", won(70_000));

        // when: 2주(14만원)를 사려고 하면
        OrderRequest.Create request = buyMarket(stock.getCode(), 2);

        // then
        assertThatThrownBy(() -> orderService.createOrder(user.getLoginId(), request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INSUFFICIENT_CASH);

        // 접수 단계에서 막혔으므로 주문 레코드도, 잔액 변동도 없어야 한다.
        assertThat(orderRepository.count()).isZero();
        assertThat(reload(user).getCash()).isEqualByComparingTo(won(100_000));
        assertThat(cashHistoryRepository.countByUserId(user.getId())).isZero();
    }

    @Test
    @DisplayName("시장가 매수가 체결되면 잔액이 차감되고 BUY 이력이 정확히 1건 남는다")
    void deductCashAndRecordHistoryOnBuy() {
        // given
        User user = createUser("buyer", won(1_000_000));
        Stock stock = createStock("005930", "삼성전자", won(70_000));

        // when: 3주 매수 → 21만원
        OrderResponse.Detail result = orderService.createOrder(user.getLoginId(), buyMarket(stock.getCode(), 3));

        // then: 주문 상태
        assertThat(result.getStatus()).isEqualTo(Order.OrderStatus.FILLED.name());
        assertThat(result.getTotalAmount()).isEqualByComparingTo(won(210_000));

        // 잔액
        assertThat(reload(user).getCash()).isEqualByComparingTo(won(790_000));

        // 이력: 부호를 포함한 음수로 남고, balanceAfter 스냅샷이 실제 잔액과 일치해야 한다
        List<CashHistory> histories = cashHistoryRepository.findByUserIdOrderByIdAsc(user.getId());
        assertThat(histories).hasSize(1);
        assertThat(histories.get(0).getType()).isEqualTo(CashHistory.TransactionType.BUY);
        assertThat(histories.get(0).getAmount()).isEqualByComparingTo(won(-210_000));
        assertThat(histories.get(0).getBalanceAfter()).isEqualByComparingTo(won(790_000));
        assertThat(histories.get(0).getOrderId()).isEqualTo(result.getId());

        // 보유 종목
        Holding holding = holdingRepository.findByUserIdAndStockId(user.getId(), stock.getId()).orElseThrow();
        assertThat(holding.getQuantity()).isEqualTo(3);
        assertThat(holding.getAveragePrice()).isEqualByComparingTo(won(70_000));
    }

    @Test
    @DisplayName("매도가 체결되면 잔액이 증가하고 SELL 이력이 남는다")
    void addCashAndRecordHistoryOnSell() {
        // given: 매수로 보유를 만든 뒤
        User user = createUser("trader", won(1_000_000));
        Stock stock = createStock("005930", "삼성전자", won(70_000));
        orderService.createOrder(user.getLoginId(), buyMarket(stock.getCode(), 5)); // -350,000 → 650,000

        // when: 2주 매도 → +140,000
        orderService.createOrder(user.getLoginId(), sellMarket(stock.getCode(), 2));

        // then
        assertThat(reload(user).getCash()).isEqualByComparingTo(won(790_000));

        List<CashHistory> histories = cashHistoryRepository.findByUserIdOrderByIdAsc(user.getId());
        assertThat(histories).hasSize(2);
        assertThat(histories.get(1).getType()).isEqualTo(CashHistory.TransactionType.SELL);
        assertThat(histories.get(1).getAmount()).isEqualByComparingTo(won(140_000));
        assertThat(histories.get(1).getBalanceAfter()).isEqualByComparingTo(won(790_000));

        // 보유 수량은 3주 남는다
        Holding holding = holdingRepository.findByUserIdAndStockId(user.getId(), stock.getId()).orElseThrow();
        assertThat(holding.getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("이력의 변동액 합계는 항상 (현재 잔액 - 최초 잔액) 과 일치한다")
    void historySumMatchesBalanceDelta() {
        // given
        BigDecimal initialCash = won(1_000_000);
        User user = createUser("auditor", initialCash);
        Stock stock = createStock("005930", "삼성전자", won(70_000));

        // when: 매수 → 매도 → 매수
        orderService.createOrder(user.getLoginId(), buyMarket(stock.getCode(), 4));
        orderService.createOrder(user.getLoginId(), sellMarket(stock.getCode(), 1));
        orderService.createOrder(user.getLoginId(), buyMarket(stock.getCode(), 2));

        // then: 이력만으로 잔액을 복원할 수 있어야 감사 로그로서 의미가 있다
        BigDecimal historySum = cashHistoryRepository.sumAmountByUserId(user.getId());
        BigDecimal actualCash = reload(user).getCash();

        assertThat(initialCash.add(historySum)).isEqualByComparingTo(actualCash);
    }

    private User reload(User user) {
        return userRepository.findById(user.getId()).orElseThrow();
    }

    private OrderRequest.Create buyMarket(String stockCode, int quantity) {
        return OrderRequest.Create.builder()
                .stockCode(stockCode)
                .orderType(Order.OrderType.MARKET)
                .side(Order.OrderSide.BUY)
                .quantity(quantity)
                .build();
    }

    private OrderRequest.Create sellMarket(String stockCode, int quantity) {
        return OrderRequest.Create.builder()
                .stockCode(stockCode)
                .orderType(Order.OrderType.MARKET)
                .side(Order.OrderSide.SELL)
                .quantity(quantity)
                .build();
    }
}
