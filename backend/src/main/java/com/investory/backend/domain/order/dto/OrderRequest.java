package com.investory.backend.domain.order.dto;

import com.investory.backend.domain.order.entity.Order;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class OrderRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create {
        @NotBlank(message = "종목 코드는 필수입니다.")
        private String stockCode;

        @NotNull(message = "주문 유형은 필수입니다.")
        private Order.OrderType orderType;

        @NotNull(message = "매매 구분은 필수입니다.")
        private Order.OrderSide side;

        @NotNull(message = "수량은 필수입니다.")
        @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
        private Integer quantity;

        private BigDecimal price;  // 지정가인 경우
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cancel {
        private String reason;
    }
}
