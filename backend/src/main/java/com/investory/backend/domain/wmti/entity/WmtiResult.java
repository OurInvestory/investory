package com.investory.backend.domain.wmti.entity;

import com.investory.backend.domain.user.entity.User;
import com.investory.backend.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wmti_results")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WmtiResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String resultType;

    @Column(nullable = false)
    private Integer stabilityScore;

    @Column(nullable = false)
    private Integer growthScore;

    @Column(nullable = false)
    private Integer riskScore;

    @Column(nullable = false)
    private Integer incomeScore;

    @Column(length = 1000)
    private String answers;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String recommendation;

    public static WmtiResult create(User user, String resultType, int stability, int growth, int risk, int income, String answers) {
        String description = getDescription(resultType);
        String recommendation = getRecommendation(resultType);

        return WmtiResult.builder()
                .user(user)
                .resultType(resultType)
                .stabilityScore(stability)
                .growthScore(growth)
                .riskScore(risk)
                .incomeScore(income)
                .answers(answers)
                .description(description)
                .recommendation(recommendation)
                .build();
    }

    private static String getDescription(String type) {
        return switch (type) {
            case "WMTI-C" -> "안정적인 투자를 선호하며, 원금 보존을 가장 중요하게 생각합니다. 리스크가 낮은 자산에 투자하는 것을 권장합니다.";
            case "WMTI-M" -> "적당한 위험을 감수하면서 수익을 추구합니다. 균형 잡힌 포트폴리오 구성을 권장합니다.";
            case "WMTI-A" -> "높은 수익을 위해 적극적으로 위험을 감수합니다. 성장주 중심의 공격적인 투자 전략을 권장합니다.";
            default -> "투자 성향 분석 결과입니다.";
        };
    }

    private static String getRecommendation(String type) {
        return switch (type) {
            case "WMTI-C" -> "국채, 우량 회사채, 배당주, MMF";
            case "WMTI-M" -> "ETF, 대형 가치주, 배당성장주, 채권혼합형 펀드";
            case "WMTI-A" -> "성장주, 신흥국 주식, 섹터 ETF, 개별 종목";
            default -> "다양한 자산에 분산 투자";
        };
    }
}
