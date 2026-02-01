package com.investory.backend.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SignUp {
        @NotBlank(message = "아이디는 필수입니다.")
        @Size(min = 4, max = 20, message = "아이디는 4~20자 사이여야 합니다.")
        private String loginId;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
        private String password;

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2~10자 사이여야 합니다.")
        private String nickname;

        private String phone;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Login {
        @NotBlank(message = "아이디는 필수입니다.")
        private String loginId;

        @NotBlank(message = "비밀번호는 필수입니다.")
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshToken {
        @NotBlank(message = "리프레시 토큰은 필수입니다.")
        private String refreshToken;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocialLogin {
        @NotBlank(message = "소셜 제공자는 필수입니다.")
        private String provider;

        @NotBlank(message = "액세스 토큰은 필수입니다.")
        private String accessToken;
    }
}
