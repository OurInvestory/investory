package com.investory.backend.domain.stock.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class StockRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddWatchlist {
        @NotBlank(message = "종목 코드는 필수입니다.")
        private String stockCode;

        private String groupName;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateWatchlistGroup {
        private String groupName;
    }
}
