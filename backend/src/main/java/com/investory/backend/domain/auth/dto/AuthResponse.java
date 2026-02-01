package com.investory.backend.domain.auth.dto;

import com.investory.backend.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Token {
        private String accessToken;
        private String refreshToken;
        private Long expiresIn;
        private UserInfo user;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long id;
        private String loginId;
        private String email;
        private String nickname;
        private String profileImage;
        private Integer level;
        private Integer experience;
        private String wmtiType;
        private String role;

        public static UserInfo from(User user) {
            return UserInfo.builder()
                    .id(user.getId())
                    .loginId(user.getLoginId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .profileImage(user.getProfileImage())
                    .level(user.getLevel())
                    .experience(user.getExperience())
                    .wmtiType(user.getWmtiType())
                    .role(user.getRole().name())
                    .build();
        }
    }
}
