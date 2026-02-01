package com.investory.backend.domain.portfolio.dto;

import com.investory.backend.domain.portfolio.entity.Holding;
import com.investory.backend.domain.stock.entity.Stock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class PortfolioResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private BigDecimal totalValue;
        private BigDecimal totalInvestment;
        private BigDecimal totalProfitLoss;
        private BigDecimal totalProfitLossRate;
        private BigDecimal dailyProfitLoss;
        private BigDecimal dailyProfitLossRate;
        private int holdingCount;
        private List<HoldingItem> holdings;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HoldingItem {
        private Long id;
        private String stockCode;
        private String stockName;
        private String market;
        private String logoUrl;
        private Integer quantity;
        private BigDecimal averagePrice;
        private BigDecimal currentPrice;
        private BigDecimal totalInvestment;
        private BigDecimal currentValue;
        private BigDecimal profitLoss;
        private BigDecimal profitLossRate;
        private BigDecimal weight;

        public static HoldingItem from(Holding holding, BigDecimal totalPortfolioValue) {
            Stock stock = holding.getStock();
            BigDecimal currentValue = holding.getCurrentValue(stock.getCurrentPrice());
            BigDecimal profitLoss = holding.getProfitLoss(stock.getCurrentPrice());
            BigDecimal profitLossRate = holding.getProfitLossRate(stock.getCurrentPrice());
            BigDecimal weight = totalPortfolioValue.compareTo(BigDecimal.ZERO) > 0
                    ? currentValue.divide(totalPortfolioValue, 4, java.math.RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;

            return HoldingItem.builder()
                    .id(holding.getId())
                    .stockCode(stock.getCode())
                    .stockName(stock.getName())
                    .market(stock.getMarket().name())
                    .logoUrl(stock.getLogoUrl())
                    .quantity(holding.getQuantity())
                    .averagePrice(holding.getAveragePrice())
                    .currentPrice(stock.getCurrentPrice())
                    .totalInvestment(holding.getTotalInvestment())
                    .currentValue(currentValue)
                    .profitLoss(profitLoss)
                    .profitLossRate(profitLossRate)
                    .weight(weight)
                    .build();
        }
    }
}
