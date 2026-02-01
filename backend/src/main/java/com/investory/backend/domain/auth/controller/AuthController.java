package com.investory.backend.domain.auth.controller;

import com.investory.backend.domain.auth.dto.AuthRequest;
import com.investory.backend.domain.auth.dto.AuthResponse;
import com.investory.backend.domain.auth.service.AuthService;
import com.investory.backend.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "인증", description = "인증 관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse.Token>> signUp(@Valid @RequestBody AuthRequest.SignUp request) {
        AuthResponse.Token response = authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("회원가입 성공", response));
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse.Token>> login(@Valid @RequestBody AuthRequest.Login request) {
        AuthResponse.Token response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰으로 새로운 액세스 토큰을 발급받습니다.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse.Token>> refresh(@Valid @RequestBody AuthRequest.RefreshToken request) {
        AuthResponse.Token response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success("토큰 갱신 성공", response));
    }

    @Operation(summary = "아이디 중복 확인", description = "아이디 사용 가능 여부를 확인합니다.")
    @GetMapping("/check-login-id")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkLoginId(@RequestParam String loginId) {
        boolean available = authService.checkLoginIdAvailable(loginId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("available", available)));
    }

    @Operation(summary = "이메일 중복 확인", description = "이메일 사용 가능 여부를 확인합니다.")
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkEmail(@RequestParam String email) {
        boolean available = authService.checkEmailAvailable(email);
        return ResponseEntity.ok(ApiResponse.success(Map.of("available", available)));
    }

    @Operation(summary = "닉네임 중복 확인", description = "닉네임 사용 가능 여부를 확인합니다.")
    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkNickname(@RequestParam String nickname) {
        boolean available = authService.checkNicknameAvailable(nickname);
        return ResponseEntity.ok(ApiResponse.success(Map.of("available", available)));
    }
}
