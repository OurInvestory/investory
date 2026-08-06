package com.investory.backend.domain.order;

import com.investory.backend.domain.order.dto.OrderRequest;
import com.investory.backend.domain.order.entity.Order;
import com.investory.backend.domain.order.service.OrderService;
import com.investory.backend.domain.portfolio.entity.Holding;
import com.investory.backend.domain.stock.entity.Stock;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.support.IntegrationTestSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Phase 4-2 검증: 동일 유저에 대한 동시 매수 주문.
 * <p>
 * <b>이 테스트가 검증하는 것</b>
 * <ol>
 *   <li>최종 잔액이 음수가 아닐 것 — 오버셀링(잔액 초과 매수)이 없어야 한다</li>
 *   <li>차감 합계 = 체결 주문 금액 합계 — 갱신 유실(lost update)이 없어야 한다</li>
 *   <li>Holding 수량 = 체결 수량 합계 — 보유 수량도 같은 원자성을 지켜야 한다</li>
 * </ol>
 * <p>
 * <b>왜 이 테스트가 없으면 안 되는가</b><br>
 * 낙관적 락은 "충돌이 났을 때 예외를 던지는" 장치일 뿐, 그 자체로 정합성을 보장하지 않는다.
 * 재시도가 붙어야 비로소 사용자 관점에서 주문이 성공한다. 그리고 재시도가 실제로 동작하는지는
 * 스레드를 실제로 띄워 보는 것 외에 확인할 방법이 없다.
 */
@Slf4j
@DisplayName("동시 주문 통합 테스트")
class ConcurrentOrderIntegrationTest extends IntegrationTestSupport {

    private static final int THREAD_COUNT = 32;
    private static final int QUANTITY_PER_ORDER = 1;
    private static final BigDecimal STOCK_PRICE = BigDecimal.valueOf(70_000);

    @Autowired
    private OrderService orderService;

    @Value("${investory.order.lock-mode:optimistic}")
    private String lockMode;

    @Test
    @DisplayName("32개 스레드가 동시에 매수해도 잔액·이력·보유 수량이 모두 정합하다")
    void concurrentBuyOrdersKeepBalanceConsistent() throws InterruptedException {
        // given: 32건을 전부 체결하고도 남는 잔액 (부족으로 인한 실패와 락 충돌을 섞지 않기 위함)
        BigDecimal initialCash = STOCK_PRICE
                .multiply(BigDecimal.valueOf(THREAD_COUNT))
                .multiply(BigDecimal.valueOf(2));

        User user = createUser("concurrent", initialCash);
        Stock stock = createStock("005930", "삼성전자", STOCK_PRICE);

        orderService.resetAttemptCount();

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        // when
        long elapsedMs = runConcurrently(THREAD_COUNT, () -> {
            try {
                orderService.createOrder(user.getLoginId(), buyMarket(stock.getCode()));
                successCount.incrementAndGet();
            } catch (Exception e) {
                failureCount.incrementAndGet();
                log.warn("주문 실패: {}", e.toString());
            }
        });

        // then
        User reloaded = userRepository.findById(user.getId()).orElseThrow();
        List<Order> filledOrders = orderRepository.findByUserIdAndStatus(user.getId(), Order.OrderStatus.FILLED);

        BigDecimal filledTotal = filledOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // (1) 잔액이 음수가 아닐 것
        assertThat(reloaded.getCash()).isGreaterThanOrEqualTo(BigDecimal.ZERO);

        // (2) 차감 합계 == 체결 금액 합계
        assertThat(initialCash.subtract(reloaded.getCash())).isEqualByComparingTo(filledTotal);

        // (3) 이력 합계도 동일해야 한다 (감사 로그 누락 없음)
        assertThat(cashHistoryRepository.sumAmountByUserId(user.getId()).negate())
                .isEqualByComparingTo(filledTotal);

        // (4) 보유 수량 == 체결 수량 합계
        int filledQuantity = filledOrders.stream().mapToInt(Order::getFilledQuantity).sum();
        Holding holding = holdingRepository.findByUserIdAndStockId(user.getId(), stock.getId()).orElseThrow();
        assertThat(holding.getQuantity()).isEqualTo(filledQuantity);

        // (5) 재시도가 붙었으므로 32건 전부 성공해야 한다
        assertThat(successCount.get()).isEqualTo(THREAD_COUNT);
        assertThat(failureCount.get()).isZero();

        // 비교 실험용 리포트. 낙관적 락이면 시도 횟수 > 32 (재시도 발생), 비관적 락이면 == 32 여야 한다.
        long attempts = orderService.getAttemptCount();
        log.info("""

                        ===== 동시성 실험 리포트 =====
                        락 전략      : {}
                        스레드 수    : {}
                        총 소요 시간 : {} ms
                        총 시도 횟수 : {} (재시도 {}회)
                        성공/실패    : {}/{}
                        최종 잔액    : {}
                        ============================""",
                lockMode, THREAD_COUNT, elapsedMs, attempts, attempts - THREAD_COUNT,
                successCount.get(), failureCount.get(), reloaded.getCash());
    }

    @Test
    @DisplayName("잔액이 절반뿐이면 초과분은 거부되고 잔액은 절대 음수가 되지 않는다")
    void concurrentBuyOrdersNeverOverdraw() throws InterruptedException {
        // given: 딱 16주만 살 수 있는 잔액으로 32개 스레드가 달려든다
        int affordableCount = THREAD_COUNT / 2;
        BigDecimal initialCash = STOCK_PRICE.multiply(BigDecimal.valueOf(affordableCount));

        User user = createUser("overdraw", initialCash);
        Stock stock = createStock("005930", "삼성전자", STOCK_PRICE);

        AtomicInteger successCount = new AtomicInteger();

        // when
        runConcurrently(THREAD_COUNT, () -> {
            try {
                orderService.createOrder(user.getLoginId(), buyMarket(stock.getCode()));
                successCount.incrementAndGet();
            } catch (Exception ignored) {
                // 잔액 부족(INSUFFICIENT_CASH)은 이 시나리오에서 기대되는 정상 동작이다
            }
        });

        // then: 핵심은 "음수가 아니다"와 "살 수 있는 만큼만 샀다"
        User reloaded = userRepository.findById(user.getId()).orElseThrow();
        assertThat(reloaded.getCash()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        assertThat(successCount.get()).isLessThanOrEqualTo(affordableCount);

        BigDecimal spent = initialCash.subtract(reloaded.getCash());
        assertThat(spent).isEqualByComparingTo(
                STOCK_PRICE.multiply(BigDecimal.valueOf(successCount.get())));
    }

    /**
     * 모든 스레드를 최대한 같은 순간에 출발시킨다.
     * <p>
     * 시작 래치를 쓰지 않고 그냥 submit 하면 스레드 풀이 순차적으로 실행해 버려
     * 충돌이 거의 재현되지 않는다. 게이트를 열어 동시에 진입시키는 것이 핵심이다.
     *
     * @return 총 소요 시간(ms)
     */
    private long runConcurrently(int threadCount, Runnable task) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                readyLatch.countDown();
                try {
                    startGate.await();
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await(10, TimeUnit.SECONDS);
        long start = System.currentTimeMillis();
        startGate.countDown();
        boolean finished = doneLatch.await(60, TimeUnit.SECONDS);
        long elapsed = System.currentTimeMillis() - start;

        executor.shutdown();
        assertThat(finished).as("모든 주문이 60초 안에 끝나야 한다 (데드락/락 대기 확인)").isTrue();

        return elapsed;
    }

    private OrderRequest.Create buyMarket(String stockCode) {
        return OrderRequest.Create.builder()
                .stockCode(stockCode)
                .orderType(Order.OrderType.MARKET)
                .side(Order.OrderSide.BUY)
                .quantity(QUANTITY_PER_ORDER)
                .build();
    }
}
