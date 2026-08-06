package com.investory.backend.domain.order.service;

import com.investory.backend.domain.order.entity.Order;
import com.investory.backend.domain.order.repository.OrderRepository;
import com.investory.backend.domain.stock.event.StockPriceUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.util.List;

/**
 * 지정가(LIMIT) 주문 매칭기.
 * <p>
 * 시세가 갱신될 때마다 해당 종목의 미체결 지정가 주문을 훑어, 체결 조건을 만족하는 주문을
 * {@link OrderExecutor} 로 넘긴다.
 * <p>
 * <b>이 클래스에 @Transactional 이 없는 이유</b><br>
 * 매처가 트랜잭션을 열면 100건을 체결하다 마지막 1건이 실패했을 때 앞의 99건이 전부 롤백된다.
 * 매처는 트랜잭션 밖에서 돌고, 체결 1건당 {@code OrderExecutor} 가 트랜잭션을 연다.
 * 실패는 그 주문 하나로 격리된다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LimitOrderMatcher {

    /** 한 번에 읽어올 주문 수. 대량 주문 상황에서 메모리를 폭발시키지 않기 위한 상한. */
    private static final int BATCH_SIZE = 100;

    /** 한 틱에서 종목당 처리할 최대 배치 수. 무한 루프 방어용 안전장치. */
    private static final int MAX_BATCHES_PER_TICK = 50;

    private final OrderRepository orderRepository;
    private final OrderExecutor orderExecutor;

    /**
     * 시세 갱신 커밋 이후에 매칭을 시작한다.
     * <p>
     * {@code AFTER_COMMIT} 이라 이 메서드 진입 시점에는 활성 트랜잭션이 없다.
     * 조회는 {@code readOnly} 트랜잭션을 별도로 열고, 체결은 주문별 트랜잭션에서 처리한다.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPriceUpdated(StockPriceUpdatedEvent event) {
        for (StockPriceUpdatedEvent.PriceTick tick : event.ticks()) {
            try {
                match(tick.stockId(), tick.currentPrice());
            } catch (Exception e) {
                // 한 종목의 매칭 실패가 나머지 종목 매칭을 막으면 안 된다.
                log.error("지정가 매칭 실패: stockCode={}", tick.code(), e);
            }
        }
    }

    /**
     * 특정 종목의 미체결 지정가 주문을 현재가 기준으로 매칭한다.
     *
     * @return 체결된 주문 수
     */
    public int match(Long stockId, BigDecimal currentPrice) {
        Pageable batch = PageRequest.of(0, BATCH_SIZE);
        long lastSeenId = 0L;
        int filled = 0;

        for (int i = 0; i < MAX_BATCHES_PER_TICK; i++) {
            List<Order> candidates = loadCandidates(stockId, lastSeenId, batch);
            if (candidates.isEmpty()) {
                break;
            }

            for (Order candidate : candidates) {
                lastSeenId = candidate.getId();

                // 체결 조건 판정은 엔티티가 갖고 있다(Order#isMatchable).
                // 매처는 "누구를 볼지"만 정하고 "체결되는지"는 도메인이 답한다.
                if (!candidate.isMatchable(currentPrice)) {
                    continue;
                }

                if (executeSafely(candidate.getId(), currentPrice)) {
                    filled++;
                }
            }

            if (candidates.size() < BATCH_SIZE) {
                break;
            }
        }

        if (filled > 0) {
            log.info("지정가 체결: stockId={}, price={}, 체결건수={}", stockId, currentPrice, filled);
        }
        return filled;
    }

    /**
     * 후보 조회.
     * <p>
     * 이 메서드에 {@code @Transactional} 을 붙이고 같은 클래스에서 호출하면
     * 이번 리팩토링에서 걷어낸 셀프 인보케이션을 그대로 재현하게 된다(프록시를 안 타므로 무효).
     * Spring Data 리포지토리 구현({@code SimpleJpaRepository})은 이미 클래스 레벨에
     * {@code @Transactional(readOnly = true)} 가 걸려 있어, 트랜잭션 밖에서 호출하면
     * 조회 단위로 읽기 전용 트랜잭션이 알아서 열린다. 별도 래핑이 필요 없다.
     * <p>
     * 여기서 읽은 엔티티는 메서드 종료와 함께 준영속 상태가 되지만, 체결 시
     * {@code executeById} 가 트랜잭션 안에서 다시 읽으므로 문제되지 않는다.
     */
    private List<Order> loadCandidates(Long stockId, Long lastSeenId, Pageable pageable) {
        return orderRepository.findMatchableOrders(
                stockId, Order.OrderStatus.PENDING, Order.OrderType.LIMIT, lastSeenId, pageable);
    }

    private boolean executeSafely(Long orderId, BigDecimal currentPrice) {
        try {
            return orderExecutor.executeById(orderId, currentPrice);
        } catch (ObjectOptimisticLockingFailureException e) {
            // 사용자가 같은 순간에 취소했거나 다른 주문이 잔액을 건드린 경우다.
            // 다음 틱에서 다시 후보로 잡히므로 여기서는 넘어간다.
            log.debug("지정가 체결 충돌 - 다음 틱에 재시도: orderId={}", orderId);
            return false;
        } catch (Exception e) {
            log.error("지정가 체결 중 예외: orderId={}", orderId, e);
            return false;
        }
    }
}
