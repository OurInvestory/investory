package com.investory.backend.domain.reward.repository;

import com.investory.backend.domain.reward.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    
    @Query("SELECT ua FROM UserAchievement ua JOIN FETCH ua.achievement WHERE ua.user.id = :userId")
    List<UserAchievement> findByUserIdWithAchievement(@Param("userId") Long userId);
    
    @Query("SELECT ua FROM UserAchievement ua JOIN FETCH ua.achievement WHERE ua.user.id = :userId AND ua.isUnlocked = true")
    List<UserAchievement> findUnlockedByUserId(@Param("userId") Long userId);
    
    @Query("SELECT ua FROM UserAchievement ua JOIN FETCH ua.achievement WHERE ua.user.id = :userId AND ua.isUnlocked = false")
    List<UserAchievement> findLockedByUserId(@Param("userId") Long userId);
    
    Optional<UserAchievement> findByUserIdAndAchievementId(Long userId, Long achievementId);
    
    boolean existsByUserIdAndAchievementId(Long userId, Long achievementId);
    
    long countByUserIdAndIsUnlockedTrue(Long userId);
}
