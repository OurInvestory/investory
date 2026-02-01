package com.investory.backend.domain.user.controller;

import com.investory.backend.domain.user.dto.UserRequest;
import com.investory.backend.domain.user.dto.UserResponse;
import com.investory.backend.domain.user.service.UserService;
import com.investory.backend.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자", description = "사용자 관련 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 프로필 조회", description = "로그인한 사용자의 프로필을 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse.Profile>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse.Profile profile = userService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @Operation(summary = "사용자 프로필 조회", description = "특정 사용자의 프로필을 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse.Profile>> getProfile(@PathVariable Long userId) {
        UserResponse.Profile profile = userService.getProfileById(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @Operation(summary = "프로필 수정", description = "내 프로필 정보를 수정합니다.")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse.Profile>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserRequest.UpdateProfile request) {
        UserResponse.Profile profile = userService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("프로필 수정 완료", profile));
    }

    @Operation(summary = "비밀번호 변경", description = "비밀번호를 변경합니다.")
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserRequest.UpdatePassword request) {
        userService.updatePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 변경 완료", null));
    }

    @Operation(summary = "회원 탈퇴", description = "계정을 비활성화합니다.")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deactivateAccount(
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.deactivateAccount(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴 완료", null));
    }
}
