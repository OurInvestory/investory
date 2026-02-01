package com.investory.backend.domain.reward.entity;

import com.investory.backend.domain.user.entity.User;
import com.investory.backend.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_achievements", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "achievement_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserAchievement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isUnlocked = false;

    private LocalDateTime unlockedAt;

    @Column
    @Builder.Default
    private Integer progress = 0;

    public void unlock() {
        this.isUnlocked = true;
        this.unlockedAt = LocalDateTime.now();
    }

    public void updateProgress(int progress) {
        this.progress = progress;
    }
}
