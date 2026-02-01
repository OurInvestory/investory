package com.investory.backend.domain.auth.service;

import com.investory.backend.domain.auth.dto.AuthRequest;
import com.investory.backend.domain.auth.dto.AuthResponse;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.domain.user.repository.UserRepository;
import com.investory.backend.global.exception.BusinessException;
import com.investory.backend.global.exception.ErrorCode;
import com.investory.backend.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse.Token signUp(AuthRequest.SignUp request) {
        // 중복 체크
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // 사용자 생성
        User user = User.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .nickname(request.getNickname())
                .phone(request.getPhone())
                .build();

        User savedUser = userRepository.save(user);
        log.info("새 사용자 가입: {}", savedUser.getLoginId());

        // 토큰 생성
        return createTokenResponse(savedUser);
    }

    @Transactional
    public AuthResponse.Token login(AuthRequest.Login request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword())
            );

            User user = userRepository.findByLoginId(request.getLoginId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            user.updateLastLogin();
            log.info("사용자 로그인: {}", user.getLoginId());

            return createTokenResponse(user);
        } catch (Exception e) {
            log.error("로그인 실패: {}", request.getLoginId());
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }
    }

    @Transactional
    public AuthResponse.Token refresh(AuthRequest.RefreshToken request) {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        String loginId = jwtTokenProvider.getSubject(request.getRefreshToken());
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return createTokenResponse(user);
    }

    private AuthResponse.Token createTokenResponse(User user) {
        String authorities = "ROLE_" + user.getRole().name();
        String accessToken = jwtTokenProvider.createAccessToken(user.getLoginId(), authorities);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getLoginId(), authorities);

        return AuthResponse.Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .user(AuthResponse.UserInfo.from(user))
                .build();
    }

    @Transactional(readOnly = true)
    public boolean checkLoginIdAvailable(String loginId) {
        return !userRepository.existsByLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public boolean checkEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean checkNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }
}
