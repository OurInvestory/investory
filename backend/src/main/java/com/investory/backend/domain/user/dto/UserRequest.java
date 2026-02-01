package com.investory.backend.domain.user.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateProfile {
        @Size(min = 2, max = 10, message = "닉네임은 2~10자 사이여야 합니다.")
        private String nickname;

        private String phone;

        private String profileImage;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePassword {
        private String currentPassword;
        private String newPassword;
    }
}
