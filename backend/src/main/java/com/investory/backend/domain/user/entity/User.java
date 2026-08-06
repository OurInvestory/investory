package com.investory.backend.domain.user.entity;

import com.investory.backend.global.common.dto.BaseEntity;
import com.investory.backend.global.exception.BusinessException;
import com.investory.backend.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_login_id", columnList = "loginId"),
    @Index(name = "idx_user_email", columnList = "email")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(length = 20)
    private String phone;

    @Column(length = 500)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AuthProvider provider = AuthProvider.LOCAL;

    @Column(length = 100)
    private String providerId;

    /**
     * 주문 가능 현금 잔액.
     * <p>
     * precision 19 / scale 2 는 "원 단위 소수점 2자리"를 표현하면서
     * BIGINT(19자리) 범위와 어긋나지 않게 맞춘 값이다.
     * 금액은 절대 double/float 로 다루지 않는다 (부동소수점 누적 오차).
     */
    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal cash = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Integer level = 1;

    @Column(nullable = false)
    @Builder.Default
    private Integer experience = 0;

    @Column(length = 20)
    private String wmtiType;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    private LocalDateTime lastLoginAt;

    // Update methods
    public void updateProfile(String nickname, String phone, String profileImage) {
        if (nickname != null) this.nickname = nickname;
        if (phone != null) this.phone = phone;
        if (profileImage != null) this.profileImage = profileImage;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * 잔액이 요청 금액을 감당할 수 있는지 확인한다.
     * 상태를 바꾸지 않는 순수 조회 메서드이므로 주문 검증 단계에서 부담 없이 호출할 수 있다.
     */
    public boolean canAfford(BigDecimal amount) {
        return this.cash.compareTo(amount) >= 0;
    }

    /**
     * 잔액을 차감한다. (매수 체결)
     * <p>
     * 잔액 검증을 엔티티 안에 두는 이유: 서비스 레이어 어디서 호출하든 "잔액이 음수가 될 수 없다"는
     * 불변식이 깨지지 않도록 도메인 객체가 스스로 보장하게 하기 위함이다.
     *
     * @return 차감 후 잔액
     */
    public BigDecimal withdraw(BigDecimal amount) {
        validatePositive(amount);
        if (!canAfford(amount)) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_CASH);
        }
        this.cash = this.cash.subtract(amount);
        return this.cash;
    }

    /**
     * 잔액을 증가시킨다. (매도 체결 / 시드머니 지급)
     *
     * @return 증가 후 잔액
     */
    public BigDecimal deposit(BigDecimal amount) {
        validatePositive(amount);
        this.cash = this.cash.add(amount);
        return this.cash;
    }

    private void validatePositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_CASH_AMOUNT);
        }
    }

    public void addExperience(int exp) {
        this.experience += exp;
        updateLevel();
    }

    private void updateLevel() {
        // 레벨 계산 로직 (경험치 기반)
        int newLevel = 1;
        int requiredExp = 0;
        while (this.experience >= requiredExp) {
            requiredExp += newLevel * 100;
            if (this.experience >= requiredExp) {
                newLevel++;
            }
        }
        this.level = newLevel;
    }

    public void updateWmtiType(String wmtiType) {
        this.wmtiType = wmtiType;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public enum Role {
        USER, ADMIN
    }

    public enum AuthProvider {
        LOCAL, GOOGLE, KAKAO, NAVER, FACEBOOK
    }
}
