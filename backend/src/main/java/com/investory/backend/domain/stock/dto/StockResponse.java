package com.investory.backend.domain.stock.dto;

import com.investory.backend.domain.stock.entity.Stock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class StockResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private Long id;
        private String code;
        private String name;
        private String market;
        private BigDecimal currentPrice;
        private BigDecimal changeRate;
        private BigDecimal changeAmount;
        private Long volume;
        private String logoUrl;

        public static Summary from(Stock stock) {
            return Summary.builder()
                    .id(stock.getId())
                    .code(stock.getCode())
                    .name(stock.getName())
                    .market(stock.getMarket().name())
                    .currentPrice(stock.getCurrentPrice())
                    .changeRate(stock.getChangeRate())
                    .changeAmount(stock.getChangeAmount())
                    .volume(stock.getVolume())
                    .logoUrl(stock.getLogoUrl())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Detail {
        private Long id;
        private String code;
        private String name;
        private String englishName;
        private String market;
        private String sector;
        private BigDecimal currentPrice;
        private BigDecimal previousClose;
        private BigDecimal changeRate;
        private BigDecimal changeAmount;
        private BigDecimal high52Week;
        private BigDecimal low52Week;
        private Long volume;
        private Long marketCap;
        private String logoUrl;

        public static Detail from(Stock stock) {
            return Detail.builder()
                    .id(stock.getId())
                    .code(stock.getCode())
                    .name(stock.getName())
                    .englishName(stock.getEnglishName())
                    .market(stock.getMarket().name())
                    .sector(stock.getSector())
                    .currentPrice(stock.getCurrentPrice())
                    .previousClose(stock.getPreviousClose())
                    .changeRate(stock.getChangeRate())
                    .changeAmount(stock.getChangeAmount())
                    .high52Week(stock.getHigh52Week())
                    .low52Week(stock.getLow52Week())
                    .volume(stock.getVolume())
                    .marketCap(stock.getMarketCap())
                    .logoUrl(stock.getLogoUrl())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WatchlistItem {
        private Long id;
        private String groupName;
        private StockResponse.Summary stock;
        private Integer sortOrder;

        public static WatchlistItem from(com.investory.backend.domain.stock.entity.Watchlist watchlist) {
            return WatchlistItem.builder()
                    .id(watchlist.getId())
                    .groupName(watchlist.getGroupName())
                    .stock(Summary.from(watchlist.getStock()))
                    .sortOrder(watchlist.getSortOrder())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderbookEntry {
        private BigDecimal price;
        private Long quantity;
        private Integer count;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Orderbook {
        private String stockCode;
        private java.util.List<OrderbookEntry> asks;
        private java.util.List<OrderbookEntry> bids;
    }
}
