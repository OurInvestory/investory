package com.investory.backend.domain.user.service;

import com.investory.backend.domain.user.config.CashProperties;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * cash 컬럼이 추가되기 전에 가입한 기존 유저에게 시드머니를 소급 지급하는 1회성 백필.
 * <p>
 * <b>왜 마이그레이션 스크립트가 아니라 러너인가</b><br>
 * 이 프로젝트는 {@code spring.jpa.hibernate.ddl-auto=update} 를 쓰고 있어서
 * 스키마 변경은 Hibernate 가 처리하지만, <b>데이터</b> 백필은 하이버네이트가 해 주지 않는다.
 * Flyway 를 지금 도입하면 이미 update 로 만들어진 기존 스키마와 베이스라인을 맞추는 작업이
 * 별도로 필요해 이번 커밋의 범위를 넘어선다. 그래서 애플리케이션 레벨 러너로 처리하고,
 * Flyway 전환은 별도 커밋으로 분리한다. (README 로드맵 참고)
 * <p>
 * <b>멱등성 보장</b><br>
 * "cash 가 0이면 지급" 이 아니라 "DEPOSIT 이력이 하나도 없으면 지급" 을 조건으로 삼는다.
 * 전자는 시드머니를 다 써서 잔액이 0이 된 유저에게 재지급해 버리는 버그가 된다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeedMoneyBackfillRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CashService cashService;
    private final CashProperties cashProperties;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!cashProperties.isBackfillExistingUsers()) {
            log.info("시드머니 백필 비활성화 상태 - 건너뜁니다.");
            return;
        }

        List<User> targets = userRepository.findUsersWithoutDepositHistory();
        if (targets.isEmpty()) {
            return;
        }

        targets.forEach(user -> cashService.deposit(user, cashProperties.getSeedMoney()));
        log.info("시드머니 백필 완료: {}명에게 {}원 지급", targets.size(), cashProperties.getSeedMoney());
    }
}
