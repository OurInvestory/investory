package com.investory.backend.domain.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 현금 도메인 정책 설정.
 * <p>
 * 시드머니 금액을 코드에 상수로 박지 않고 프로퍼티로 뺀 이유:
 * 데모/테스트/운영에서 서로 다른 금액을 쓰고 싶을 때 재빌드 없이 바꿀 수 있어야 하고,
 * 통합 테스트에서 "잔액 부족" 시나리오를 만들 때 작은 값으로 낮춰 쓰기 위함이다.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "investory.cash")
public class CashProperties {

    /** 회원가입 시 지급하는 초기 시드머니 (기본 1천만 원) */
    private BigDecimal seedMoney = new BigDecimal("10000000");

    /**
     * 애플리케이션 기동 시 cash 가 0인 기존 유저에게도 시드머니를 지급할지 여부.
     * <p>
     * ddl-auto=update 로 컬럼이 추가되면 기존 행의 cash 는 0으로 채워진다.
     * 이 값을 true 로 두면 1회성 백필이 수행되며, 이미 지급받은 유저는
     * DEPOSIT 이력 존재 여부로 걸러지므로 여러 번 재기동해도 중복 지급되지 않는다(멱등).
     */
    private boolean backfillExistingUsers = true;
}
