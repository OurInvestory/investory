package com.investory.backend.domain.reward.service;

import com.investory.backend.domain.user.entity.User;
import com.investory.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 경험치 적립.
 * <p>
 * <b>왜 여기에도 재시도가 필요한가</b><br>
 * {@code User} 엔티티에는 {@code @Version} 이 걸려 있고, 경험치 적립도 결국 같은 행에 대한
 * UPDATE 다. 동시 주문이 몰리면 <b>체결이 아니라 경험치 적립 단계에서</b> 낙관적 락 충돌이 난다.
 * 주문 경로만 재시도를 붙이고 여기를 빼놓으면, 체결은 성공했는데 경험치만 조용히 유실된다.
 * <p>
 * 주문 경로와 동일하게 재시도 경계({@link ExperienceGranter})와 트랜잭션 경계
 * ({@link Updater})를 다른 빈으로 분리했다. 이유는 {@code OrderPlacer} 주석 참고.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperienceGranter {

    private final Updater updater;

    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 30, multiplier = 2.0, maxDelay = 200, random = true)
    )
    public void grant(Long userId, int exp) {
        updater.addExperience(userId, exp);
    }

    /**
     * 재시도 소진 시.
     * 경험치는 서비스의 핵심 정합성 대상이 아니다(게이미피케이션 부가 기능).
     * 여기서 예외를 다시 던지면 이미 커밋된 체결에 대한 후처리가 실패로 기록될 뿐이므로,
     * 로그만 남기고 삼킨다. 유실량이 문제가 되면 그때 아웃박스 테이블로 승격하는 게 맞다.
     */
    @Recover
    public void recoverGrant(ObjectOptimisticLockingFailureException e, Long userId, int exp) {
        log.warn("경험치 적립 실패(재시도 소진): userId={}, exp={}", userId, exp);
    }

    /**
     * 실제 UPDATE 를 수행하는 트랜잭션 경계.
     * <p>
     * {@code REQUIRES_NEW} 인 이유: 호출자가 {@code AFTER_COMMIT} 리스너라
     * 원본 트랜잭션은 이미 커밋된 뒤다. 새 트랜잭션을 열지 않으면 변경이 flush 되지 않는다.
     */
    @Component
    @RequiredArgsConstructor
    public static class Updater {

        private final UserRepository userRepository;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void addExperience(Long userId, int exp) {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                log.warn("경험치 적립 대상 유저 없음: userId={}", userId);
                return;
            }
            user.addExperience(exp);
        }
    }
}
