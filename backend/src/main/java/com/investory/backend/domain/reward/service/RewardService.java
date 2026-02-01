package com.investory.backend.domain.reward.service;

import com.investory.backend.domain.reward.dto.RewardResponse;
import com.investory.backend.domain.reward.entity.Achievement;
import com.investory.backend.domain.reward.entity.UserAchievement;
import com.investory.backend.domain.reward.repository.AchievementRepository;
import com.investory.backend.domain.reward.repository.UserAchievementRepository;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.domain.user.repository.UserRepository;
import com.investory.backend.global.exception.BusinessException;
import com.investory.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardService {

    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;

    @Transactional(readOnly = true)
    public RewardResponse.LevelInfo getLevelInfo(String loginId) {
        User user = getUserByLoginId(loginId);
        return RewardResponse.LevelInfo.from(user.getLevel(), user.getExperience());
    }

    @Transactional(readOnly = true)
    public RewardResponse.AchievementSummary getAchievements(String loginId) {
        User user = getUserByLoginId(loginId);
        
        // 모든 업적 초기화 (없으면 생성)
        initializeUserAchievements(user);

        List<UserAchievement> userAchievements = userAchievementRepository.findByUserIdWithAchievement(user.getId());
        List<RewardResponse.AchievementItem> items = userAchievements.stream()
                .map(RewardResponse.AchievementItem::from)
                .collect(Collectors.toList());

        int unlockedCount = (int) userAchievements.stream().filter(UserAchievement::getIsUnlocked).count();
        int totalExpEarned = userAchievements.stream()
                .filter(UserAchievement::getIsUnlocked)
                .mapToInt(ua -> ua.getAchievement().getExpReward())
                .sum();

        return RewardResponse.AchievementSummary.builder()
                .totalCount(userAchievements.size())
                .unlockedCount(unlockedCount)
                .totalExpEarned(totalExpEarned)
                .achievements(items)
                .build();
    }

    @Transactional
    public void initializeUserAchievements(User user) {
        List<Achievement> allAchievements = achievementRepository.findByIsActiveTrue();
        
        for (Achievement achievement : allAchievements) {
            if (!userAchievementRepository.existsByUserIdAndAchievementId(user.getId(), achievement.getId())) {
                UserAchievement ua = UserAchievement.builder()
                        .user(user)
                        .achievement(achievement)
                        .isUnlocked(false)
                        .progress(0)
                        .build();
                userAchievementRepository.save(ua);
            }
        }
    }

    @Transactional
    public void checkAndUnlockAchievement(String loginId, String achievementCode, int progress) {
        User user = getUserByLoginId(loginId);
        Achievement achievement = achievementRepository.findByCode(achievementCode)
                .orElse(null);
        
        if (achievement == null) return;

        UserAchievement ua = userAchievementRepository.findByUserIdAndAchievementId(user.getId(), achievement.getId())
                .orElse(UserAchievement.builder()
                        .user(user)
                        .achievement(achievement)
                        .isUnlocked(false)
                        .progress(0)
                        .build());

        if (ua.getIsUnlocked()) return;

        ua.updateProgress(progress);

        // 업적 달성 조건 확인
        boolean shouldUnlock = achievement.getMaxProgress() == null 
                || progress >= achievement.getMaxProgress();

        if (shouldUnlock) {
            ua.unlock();
            user.addExperience(achievement.getExpReward());
            userRepository.save(user);
            log.info("업적 달성: {} - {} (+{}exp)", loginId, achievementCode, achievement.getExpReward());
        }

        userAchievementRepository.save(ua);
    }

    private User getUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
