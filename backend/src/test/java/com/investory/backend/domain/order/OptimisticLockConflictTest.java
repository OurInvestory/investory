package com.investory.backend.domain.order;

import com.investory.backend.domain.order.dto.OrderRequest;
import com.investory.backend.domain.order.entity.Order;
import com.investory.backend.domain.order.service.OrderPlacer;
import com.investory.backend.domain.stock.entity.Stock;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.support.IntegrationTestSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Phase 4-2 (2)번 항목: <b>실패를 먼저 재현하는 테스트</b>.
 * <p>
 * 재시도 계층({@code OrderService.@Retryable})을 건너뛰고 트랜잭션 계층
 * ({@link OrderPlacer})을 직접 때려서, {@code @Version} 만으로는
 * {@link ObjectOptimisticLockingFailureException} 이 그대로 사용자에게 노출된다는 사실을 증명한다.
 * <p>
 * <b>이 테스트의 존재 이유</b><br>
 * 재시도를 붙인 뒤 테스트가 통과하면 "원래 문제가 있긴 했나?"를 알 수 없다.
 * 문제를 재현하는 테스트가 남아 있어야, 나중에 누가 {@code @Retryable} 을 지웠을 때
 * 무엇이 깨지는지가 코드로 설명된다.
 * <p>
 * 참고: 이 테스트는 스케줄링·타이밍에 의존하므로 충돌이 <b>한 번도</b> 안 날 가능성이 이론적으로 있다.
 * 그래서 "반드시 충돌한다"가 아니라 "충돌이 나면 그 예외는 낙관적 락 예외다 + 재시도 경로에서는
 * 그런 실패가 없다"를 검증하는 형태로 작성했다.
 */
@Slf4j
@DisplayName("낙관적 락 충돌 재현 테스트")
class OptimisticLockConflictTest extends IntegrationTestSupport {

    private static final int THREAD_COUNT = 32;
    private static final BigDecimal STOCK_PRICE = BigDecimal.valueOf(70_000);

    /** 재시도 프록시를 거치지 않는 트랜잭션 계층 직접 주입. */
    @Autowired
    private OrderPlacer orderPlacer;

    @Test
    @DisplayName("재시도 없이 동시 매수하면 낙관적 락 예외가 발생하지만, 잔액 정합성은 깨지지 않는다")
    void reproduceOptimisticLockFailureWithoutRetry() throws InterruptedException {
        // given
        BigDecimal initialCash = STOCK_PRICE.multiply(BigDecimal.valueOf(THREAD_COUNT * 2L));
        User user = createUser("conflict", initialCash);
        Stock stock = createStock("005930", "삼성전자", STOCK_PRICE);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger optimisticLockFailure = new AtomicInteger();
        AtomicInteger otherFailure = new AtomicInteger();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    startGate.await();
                    orderPlacer.place(user.getLoginId(), buyMarket(stock.getCode()));
                    success.incrementAndGet();
                } catch (ObjectOptimisticLockingFailureException e) {
                    optimisticLockFailure.incrementAndGet();
                } catch (Exception e) {
                    otherFailure.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startGate.countDown();
        assertThat(doneLatch.await(60, TimeUnit.SECONDS)).isTrue();
        executor.shutdown();

        log.info("재시도 없음 - 성공 {}건 / 낙관적 락 실패 {}건 / 기타 실패 {}건",
                success.get(), optimisticLockFailure.get(), otherFailure.get());

        // then
        // 1) 낙관적 락 외의 이유로 실패한 주문은 없어야 한다 (테스트 자체의 오염 방지)
        assertThat(otherFailure.get()).isZero();

        // 2) 실패했든 성공했든, 커밋된 결과는 언제나 정합해야 한다.
        //    낙관적 락의 역할은 "충돌을 막는 것"이 아니라 "깨진 상태로 커밋되는 것을 막는 것"이다.
        User reloaded = userRepository.findById(user.getId()).orElseThrow();
        BigDecimal spent = initialCash.subtract(reloaded.getCash());

        assertThat(reloaded.getCash()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        assertThat(spent).isEqualByComparingTo(
                STOCK_PRICE.multiply(BigDecimal.valueOf(success.get())));

        // 3) 성공 건수가 32건보다 적다면, 그 차이는 전부 낙관적 락 실패로 설명되어야 한다
        assertThat(success.get() + optimisticLockFailure.get()).isEqualTo(THREAD_COUNT);
    }

    private OrderRequest.Create buyMarket(String stockCode) {
        return OrderRequest.Create.builder()
                .stockCode(stockCode)
                .orderType(Order.OrderType.MARKET)
                .side(Order.OrderSide.BUY)
                .quantity(1)
                .build();
    }
}
