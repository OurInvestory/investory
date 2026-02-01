package com.investory.backend.domain.wmti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class WmtiRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Submit {
        private List<Answer> answers;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Answer {
        private Integer questionId;
        private Integer optionId;
    }
}
