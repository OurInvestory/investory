package com.investory.backend.domain.wmti.dto;

import com.investory.backend.domain.wmti.entity.WmtiResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class WmtiResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Question {
        private Integer id;
        private String question;
        private List<Option> options;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Option {
        private Integer id;
        private String text;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Result {
        private Long id;
        private String resultType;
        private String typeName;
        private Integer stabilityScore;
        private Integer growthScore;
        private Integer riskScore;
        private Integer incomeScore;
        private String description;
        private String recommendation;
        private List<String> traits;
        private LocalDateTime createdAt;

        public static Result from(WmtiResult wmtiResult) {
            String typeName = getTypeName(wmtiResult.getResultType());
            List<String> traits = getTraits(wmtiResult.getResultType());

            return Result.builder()
                    .id(wmtiResult.getId())
                    .resultType(wmtiResult.getResultType())
                    .typeName(typeName)
                    .stabilityScore(wmtiResult.getStabilityScore())
                    .growthScore(wmtiResult.getGrowthScore())
                    .riskScore(wmtiResult.getRiskScore())
                    .incomeScore(wmtiResult.getIncomeScore())
                    .description(wmtiResult.getDescription())
                    .recommendation(wmtiResult.getRecommendation())
                    .traits(traits)
                    .createdAt(wmtiResult.getCreatedAt())
                    .build();
        }

        private static String getTypeName(String type) {
            return switch (type) {
                case "WMTI-C" -> "보수형 투자자";
                case "WMTI-M" -> "중립형 투자자";
                case "WMTI-A" -> "공격형 투자자";
                default -> "투자자";
            };
        }

        private static List<String> getTraits(String type) {
            return switch (type) {
                case "WMTI-C" -> List.of("안정 추구", "원금 보존 중시", "낮은 위험 선호", "장기 투자");
                case "WMTI-M" -> List.of("균형 추구", "적정 수익 기대", "분산 투자", "중기 투자");
                case "WMTI-A" -> List.of("수익 추구", "높은 위험 감수", "적극적 매매", "단기~중기 투자");
                default -> List.of();
            };
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionSet {
        private List<Question> questions;
        private int totalQuestions;
    }
}
