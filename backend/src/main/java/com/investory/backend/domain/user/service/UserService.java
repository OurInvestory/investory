package com.investory.backend.domain.user.service;

import com.investory.backend.domain.user.dto.UserRequest;
import com.investory.backend.domain.user.dto.UserResponse;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.domain.user.repository.UserRepository;
import com.investory.backend.global.exception.BusinessException;
import com.investory.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse.Profile getProfile(String loginId) {
        User user = getUserByLoginId(loginId);
        return UserResponse.Profile.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse.Profile getProfileById(Long userId) {
        User user = getUserById(userId);
        return UserResponse.Profile.from(user);
    }

    @Transactional
    public UserResponse.Profile updateProfile(String loginId, UserRequest.UpdateProfile request) {
        User user = getUserByLoginId(loginId);

        // 닉네임 중복 체크
        if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
            if (userRepository.existsByNickname(request.getNickname())) {
                throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
            }
        }

        user.updateProfile(request.getNickname(), request.getPhone(), request.getProfileImage());
        log.info("프로필 업데이트: {}", loginId);

        return UserResponse.Profile.from(user);
    }

    @Transactional
    public void updatePassword(String loginId, UserRequest.UpdatePassword request) {
        User user = getUserByLoginId(loginId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        log.info("비밀번호 변경: {}", loginId);
    }

    @Transactional
    public void addExperience(String loginId, int exp) {
        User user = getUserByLoginId(loginId);
        user.addExperience(exp);
        log.info("경험치 추가: {} +{}exp (현재: {}exp, Lv.{})", loginId, exp, user.getExperience(), user.getLevel());
    }

    @Transactional
    public void deactivateAccount(String loginId) {
        User user = getUserByLoginId(loginId);
        user.deactivate();
        log.info("계정 비활성화: {}", loginId);
    }

    private User getUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
