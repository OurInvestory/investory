package com.investory.backend.domain.order;

import com.investory.backend.domain.order.dto.OrderRequest;
import com.investory.backend.domain.order.dto.OrderResponse;
import com.investory.backend.domain.order.entity.Order;
import com.investory.backend.domain.order.event.OrderFilledEvent;
import com.investory.backend.domain.order.service.OrderService;
import com.investory.backend.domain.stock.entity.Stock;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.global.exception.BusinessException;
import com.investory.backend.support.IntegrationTestSupport;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Phase 4-3 검증: 게이미피케이션 분리.
 * <p>
 * {@link RecordApplicationEvents} 는 테스트 실행 중 발행된 모든 이벤트를 수집해 준다.
 * 리스너를 목으로 바꾸지 않고도 "무엇이 발행됐는가"를 검증할 수 있어, 발행 측과 수신 측을
 * 각각 독립적으로 확인할 수 있다.
 */
@RecordApplicationEvents
@DisplayName("주문 체결 이벤트 테스트")
class OrderFilledEventTest extends IntegrationTestSupport {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ApplicationEvents applicationEvents;

    @Test
    @DisplayName("체결되면 OrderFilledEvent 가 정확히 1건 발행된다")
    void publishEventOnFill() {
        // given
        User user = createUser("eventer", won(1_000_000));
        Stock stock = createStock("005930", "삼성전자", won(70_000));

        // when
        OrderResponse.Detail result = orderService.createOrder(user.getLoginId(), buyMarket(stock.getCode(), 2));

        // then
        List<OrderFilledEvent> events = applicationEvents.stream(OrderFilledEvent.class).toList();
        assertThat(events).hasSize(1);

        OrderFilledEvent event = events.get(0);
        assertThat(event.orderId()).isEqualTo(result.getId());
        assertThat(event.userId()).isEqualTo(user.getId());
        assertThat(event.loginId()).isEqualTo(user.getLoginId());
        assertThat(event.stockCode()).isEqualTo(stock.getCode());
        assertThat(event.side()).isEqualTo(Order.OrderSide.BUY);
        assertThat(event.quantity()).isEqualTo(2);
        assertThat(event.totalAmount()).isEqualByComparingTo(won(140_000));
    }

    @Test
    @DisplayName("체결이 실패하면 이벤트가 발행되지 않는다")
    void doNotPublishEventWhenOrderRejected() {
        // given: 잔액이 부족한 유저
        User user = createUser("broke", won(10_000));
        Stock stock = createStock("005930", "삼성전자", won(70_000));

        // when
        assertThatThrownBy(() -> orderService.createOrder(user.getLoginId(), buyMarket(stock.getCode(), 1)))
                .isInstanceOf(BusinessException.class);

        // then
        assertThat(applicationEvents.stream(OrderFilledEvent.class)).isEmpty();
    }

    @Test
    @DisplayName("AFTER_COMMIT 리스너가 경험치를 실제로 적립한다")
    void listenerGrantsExperienceAfterCommit() {
        // given
        User user = createUser("leveler", won(1_000_000));
        Stock stock = createStock("005930", "삼성전자", won(70_000));
        int before = user.getExperience();

        // when
        orderService.createOrder(user.getLoginId(), buyMarket(stock.getCode(), 1));

        // then: 리스너는 REQUIRES_NEW 로 별도 트랜잭션에서 커밋되므로 재조회해야 보인다.
        // 동기 실행이지만, 커밋 타이밍에 여유를 두기 위해 Awaitility 로 짧게 폴링한다.
        Awaitility.await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    User reloaded = userRepository.findById(user.getId()).orElseThrow();
                    assertThat(reloaded.getExperience()).isEqualTo(before + 20);
                });
    }

    private OrderRequest.Create buyMarket(String stockCode, int quantity) {
        return OrderRequest.Create.builder()
                .stockCode(stockCode)
                .orderType(Order.OrderType.MARKET)
                .side(Order.OrderSide.BUY)
                .quantity(quantity)
                .build();
    }
}
