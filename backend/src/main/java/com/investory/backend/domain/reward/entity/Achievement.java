package com.investory.backend.domain.reward.entity;

import com.investory.backend.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "achievements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Achievement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(length = 50)
    private String icon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementCategory category;

    @Column(nullable = false)
    @Builder.Default
    private Integer expReward = 0;

    @Column
    private Integer maxProgress;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    public enum AchievementCategory {
        TRADING, COMMUNITY, LEARNING, STREAK, PORTFOLIO
    }
}
