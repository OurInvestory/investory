package com.investory.backend.domain.order.entity;

import com.investory.backend.domain.stock.entity.Stock;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderSide side;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    @Builder.Default
    private Integer filledQuantity = 0;

    @Column(precision = 20, scale = 2)
    private BigDecimal price;

    @Column(precision = 20, scale = 2)
    private BigDecimal filledPrice;

    @Column(precision = 20, scale = 2)
    private BigDecimal totalAmount;

    private LocalDateTime filledAt;

    private LocalDateTime cancelledAt;

    @Column(length = 200)
    private String cancelReason;

    public void fill(BigDecimal executionPrice, int executedQuantity) {
        this.filledQuantity += executedQuantity;
        this.filledPrice = executionPrice;
        this.totalAmount = executionPrice.multiply(BigDecimal.valueOf(executedQuantity));
        this.filledAt = LocalDateTime.now();
        
        if (this.filledQuantity.equals(this.quantity)) {
            this.status = OrderStatus.FILLED;
        } else {
            this.status = OrderStatus.PARTIALLY_FILLED;
        }
    }

    public void cancel(String reason) {
        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelReason = reason;
    }

    public enum OrderType {
        MARKET, LIMIT
    }

    public enum OrderSide {
        BUY, SELL
    }

    public enum OrderStatus {
        PENDING, PARTIALLY_FILLED, FILLED, CANCELLED, REJECTED
    }
}
