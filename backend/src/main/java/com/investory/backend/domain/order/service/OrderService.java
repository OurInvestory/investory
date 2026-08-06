package com.investory.backend.domain.order.service;

import com.investory.backend.domain.order.dto.OrderRequest;
import com.investory.backend.domain.order.dto.OrderResponse;
import com.investory.backend.domain.order.entity.Order;
import com.investory.backend.domain.order.repository.OrderRepository;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.domain.user.repository.UserRepository;
import com.investory.backend.global.exception.BusinessException;
import com.investory.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * 주문 유스케이스 진입점.
 * <p>
 * 이 클래스의 쓰기 경로({@link #createOrder})는 <b>트랜잭션을 열지 않는다</b>.
 * 낙관적 락 충돌 재시도는 트랜잭션 바깥에서 수행해야 하며, 실제 트랜잭션은
 * {@link OrderPlacer} 가 연다. 자세한 근거는 {@code OrderPlacer} 클래스 주석 참고.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderPlacer orderPlacer;

    /**
     * 주문 시도 횟수 카운터 (최초 시도 + 재시도).
     * 낙관적 락 vs 비관적 락 비교 실험에서 재시도 횟수를 측정하기 위한 계측용이다.
     * 운영이라면 Micrometer Counter 가 정석이지만, 의존성 추가 없이 쓰려고 LongAdder 를 택했다.
     */
    private final LongAdder attemptCounter = new LongAdder();

    /**
     * 주문 생성.
     * <p>
     * {@code @Retryable} 은 {@link ObjectOptimisticLockingFailureException} 만 잡는다.
     * 잔액 부족 같은 {@code BusinessException} 은 몇 번을 다시 해도 결과가 같으므로 즉시 전파해야 한다.
     * <p>
     * 백오프에 {@code random = true} 를 준 이유: 지연이 고정이면 충돌한 스레드들이 같은 시점에
     * 동시에 재진입해 또 충돌한다(thundering herd). 지연을 흩뿌려 재충돌 확률을 낮춘다.
     */
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2.0, maxDelay = 300, random = true)
    )
    public OrderResponse.Detail createOrder(String loginId, OrderRequest.Create request) {
        attemptCounter.increment();
        return orderPlacer.place(loginId, request);
    }

    /**
     * 재시도를 모두 소진했을 때의 최종 처리.
     * <p>
     * 저수준 예외인 {@code ObjectOptimisticLockingFailureException} 이 그대로 컨트롤러까지 올라가면
     * 500 이 나간다. 도메인 예외로 번역해 409 Conflict 로 응답하고 클라이언트가 재시도하도록 유도한다.
     * {@code @Recover} 메서드는 반환 타입과 (예외를 제외한) 파라미터가 원본과 일치해야 매칭된다.
     */
    @Recover
    public OrderResponse.Detail recoverCreateOrder(ObjectOptimisticLockingFailureException e,
                                                   String loginId, OrderRequest.Create request) {
        log.warn("주문 생성 재시도 소진: loginId={}, stockCode={}", loginId, request.getStockCode(), e);
        throw new BusinessException(ErrorCode.ORDER_CONFLICT);
    }

    /** 실험용: {@link #createOrder} 본문이 실행된 총 횟수. */
    public long getAttemptCount() {
        return attemptCounter.sum();
    }

    /** 실험용: 카운터 초기화. */
    public void resetAttemptCount() {
        attemptCounter.reset();
    }

    @Transactional
    public OrderResponse.Detail cancelOrder(String loginId, Long orderId, String reason) {
        User user = getUserByLoginId(loginId);
        Order order = getOrderById(orderId);

        // 권한 확인
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 취소 가능 여부 확인
        if (!order.isPending()) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL);
        }

        order.cancel(reason != null ? reason : "사용자 취소");
        log.info("주문 취소: {} - 주문ID {}", loginId, orderId);

        return OrderResponse.Detail.from(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse.Summary> getOrders(String loginId, Pageable pageable) {
        User user = getUserByLoginId(loginId);
        return orderRepository.findByUserIdWithStock(user.getId(), pageable)
                .map(OrderResponse.Summary::from);
    }

    @Transactional(readOnly = true)
    public OrderResponse.Detail getOrderDetail(String loginId, Long orderId) {
        User user = getUserByLoginId(loginId);
        Order order = getOrderById(orderId);

        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        return OrderResponse.Detail.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse.Summary> getPendingOrders(String loginId) {
        User user = getUserByLoginId(loginId);
        return orderRepository.findByUserIdAndStatus(user.getId(), Order.OrderStatus.PENDING).stream()
                .map(OrderResponse.Summary::from)
                .collect(Collectors.toList());
    }

    private User getUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }
}
