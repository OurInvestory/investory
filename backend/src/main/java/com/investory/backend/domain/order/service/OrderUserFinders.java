package com.investory.backend.domain.order.service;

import com.investory.backend.domain.user.entity.User;
import com.investory.backend.domain.user.repository.UserRepository;
import com.investory.backend.global.exception.BusinessException;
import com.investory.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * {@link OrderUserFinder} 구현 모음.
 * <p>
 * 두 구현이 서로의 대조군이라 한 파일에 모아 두면 차이를 눈으로 바로 비교할 수 있다.
 * {@code investory.order.lock-mode} 값으로 정확히 하나만 빈으로 등록된다.
 */
public final class OrderUserFinders {

    private OrderUserFinders() {
    }

    /**
     * 기본 전략: 락 없이 조회하고, 저장 시 {@code @Version} 으로 충돌을 감지한다.
     * 충돌은 {@code OrderService} 의 {@code @Retryable} 이 재시도로 흡수한다.
     */
    @Component
    @RequiredArgsConstructor
    @ConditionalOnProperty(name = "investory.order.lock-mode", havingValue = "optimistic", matchIfMissing = true)
    public static class Optimistic implements OrderUserFinder {

        private final UserRepository userRepository;

        @Override
        public User findForOrder(String loginId) {
            return userRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        }

        @Override
        public String strategyName() {
            return "OPTIMISTIC_LOCK_WITH_RETRY";
        }
    }

    /**
     * 비교군: 조회 시점에 {@code SELECT ... FOR UPDATE} 로 행 락을 잡는다.
     * 트랜잭션이 끝날 때까지 같은 유저의 다른 주문은 대기하므로 충돌 자체가 발생하지 않고,
     * 따라서 재시도도 일어나지 않는다. 대신 대기 시간이 응답 지연으로 나타난다.
     */
    @Component
    @RequiredArgsConstructor
    @ConditionalOnProperty(name = "investory.order.lock-mode", havingValue = "pessimistic")
    public static class Pessimistic implements OrderUserFinder {

        private final UserRepository userRepository;

        @Override
        public User findForOrder(String loginId) {
            return userRepository.findByLoginIdForUpdate(loginId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        }

        @Override
        public String strategyName() {
            return "PESSIMISTIC_WRITE_LOCK";
        }
    }
}
