package com.investory.backend.domain.user.dto;

import com.investory.backend.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class UserResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Profile {
        private Long id;
        private String loginId;
        private String email;
        private String nickname;
        private String phone;
        private String profileImage;
        private Integer level;
        private Integer experience;
        private Integer requiredExp;
        private String levelTitle;
        private String wmtiType;
        private LocalDateTime createdAt;
        private LocalDateTime lastLoginAt;

        public static Profile from(User user) {
            int level = user.getLevel();
            int requiredExp = calculateRequiredExp(level);
            String levelTitle = getLevelTitle(level);

            return Profile.builder()
                    .id(user.getId())
                    .loginId(user.getLoginId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .phone(user.getPhone())
                    .profileImage(user.getProfileImage())
                    .level(level)
                    .experience(user.getExperience())
                    .requiredExp(requiredExp)
                    .levelTitle(levelTitle)
                    .wmtiType(user.getWmtiType())
                    .createdAt(user.getCreatedAt())
                    .lastLoginAt(user.getLastLoginAt())
                    .build();
        }

        private static int calculateRequiredExp(int level) {
            int required = 0;
            for (int i = 1; i <= level; i++) {
                required += i * 100;
            }
            return required;
        }

        private static String getLevelTitle(int level) {
            if (level >= 50) return "마스터";
            if (level >= 30) return "전문 투자자";
            if (level >= 20) return "숙련 투자자";
            if (level >= 10) return "중급 투자자";
            if (level >= 5) return "초보 투자자";
            return "입문자";
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Simple {
        private Long id;
        private String nickname;
        private String profileImage;
        private Integer level;

        public static Simple from(User user) {
            return Simple.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .profileImage(user.getProfileImage())
                    .level(user.getLevel())
                    .build();
        }
    }
}
