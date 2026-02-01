package com.investory.backend.domain.order.dto;

import com.investory.backend.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Detail {
        private Long id;
        private String stockCode;
        private String stockName;
        private String orderType;
        private String side;
        private String status;
        private Integer quantity;
        private Integer filledQuantity;
        private BigDecimal price;
        private BigDecimal filledPrice;
        private BigDecimal totalAmount;
        private LocalDateTime createdAt;
        private LocalDateTime filledAt;
        private LocalDateTime cancelledAt;
        private String cancelReason;

        public static Detail from(Order order) {
            return Detail.builder()
                    .id(order.getId())
                    .stockCode(order.getStock().getCode())
                    .stockName(order.getStock().getName())
                    .orderType(order.getOrderType().name())
                    .side(order.getSide().name())
                    .status(order.getStatus().name())
                    .quantity(order.getQuantity())
                    .filledQuantity(order.getFilledQuantity())
                    .price(order.getPrice())
                    .filledPrice(order.getFilledPrice())
                    .totalAmount(order.getTotalAmount())
                    .createdAt(order.getCreatedAt())
                    .filledAt(order.getFilledAt())
                    .cancelledAt(order.getCancelledAt())
                    .cancelReason(order.getCancelReason())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private Long id;
        private String stockCode;
        private String stockName;
        private String side;
        private String status;
        private Integer quantity;
        private BigDecimal totalAmount;
        private LocalDateTime createdAt;

        public static Summary from(Order order) {
            return Summary.builder()
                    .id(order.getId())
                    .stockCode(order.getStock().getCode())
                    .stockName(order.getStock().getName())
                    .side(order.getSide().name())
                    .status(order.getStatus().name())
                    .quantity(order.getQuantity())
                    .totalAmount(order.getTotalAmount())
                    .createdAt(order.getCreatedAt())
                    .build();
        }
    }
}
