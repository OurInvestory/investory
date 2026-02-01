package com.investory.backend.domain.reward.dto;

import com.investory.backend.domain.reward.entity.Achievement;
import com.investory.backend.domain.reward.entity.UserAchievement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class RewardResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LevelInfo {
        private Integer level;
        private String title;
        private Integer currentExp;
        private Integer requiredExp;
        private Integer expToNextLevel;
        private Double progressPercent;
        private List<LevelTier> tiers;

        public static LevelInfo from(int level, int exp) {
            int requiredExp = calculateRequiredExp(level);
            int prevLevelExp = level > 1 ? calculateRequiredExp(level - 1) : 0;
            int expToNextLevel = requiredExp - exp;
            double progressPercent = ((double) (exp - prevLevelExp) / (requiredExp - prevLevelExp)) * 100;

            return LevelInfo.builder()
                    .level(level)
                    .title(getLevelTitle(level))
                    .currentExp(exp)
                    .requiredExp(requiredExp)
                    .expToNextLevel(Math.max(0, expToNextLevel))
                    .progressPercent(Math.min(100, progressPercent))
                    .tiers(getLevelTiers())
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

        private static List<LevelTier> getLevelTiers() {
            return List.of(
                    LevelTier.builder().level(1).title("입문자").minExp(0).build(),
                    LevelTier.builder().level(5).title("초보 투자자").minExp(1000).build(),
                    LevelTier.builder().level(10).title("중급 투자자").minExp(5000).build(),
                    LevelTier.builder().level(20).title("숙련 투자자").minExp(15000).build(),
                    LevelTier.builder().level(30).title("전문 투자자").minExp(35000).build(),
                    LevelTier.builder().level(50).title("마스터").minExp(75000).build()
            );
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LevelTier {
        private Integer level;
        private String title;
        private Integer minExp;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AchievementItem {
        private Long id;
        private String code;
        private String name;
        private String description;
        private String icon;
        private String category;
        private Integer expReward;
        private Integer maxProgress;
        private Integer currentProgress;
        private Boolean isUnlocked;
        private LocalDateTime unlockedAt;

        public static AchievementItem from(UserAchievement ua) {
            Achievement achievement = ua.getAchievement();
            return AchievementItem.builder()
                    .id(achievement.getId())
                    .code(achievement.getCode())
                    .name(achievement.getName())
                    .description(achievement.getDescription())
                    .icon(achievement.getIcon())
                    .category(achievement.getCategory().name())
                    .expReward(achievement.getExpReward())
                    .maxProgress(achievement.getMaxProgress())
                    .currentProgress(ua.getProgress())
                    .isUnlocked(ua.getIsUnlocked())
                    .unlockedAt(ua.getUnlockedAt())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AchievementSummary {
        private Integer totalCount;
        private Integer unlockedCount;
        private Integer totalExpEarned;
        private List<AchievementItem> achievements;
    }
}
