package com.investory.backend.domain.user.entity;

import com.investory.backend.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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
