package com.investory.backend.domain.reward.repository;

import com.investory.backend.domain.reward.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    
    Optional<Achievement> findByCode(String code);
    
    List<Achievement> findByCategory(Achievement.AchievementCategory category);
    
    List<Achievement> findByIsActiveTrue();
}
