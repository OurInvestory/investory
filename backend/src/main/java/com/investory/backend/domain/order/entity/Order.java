package com.investory.backend.domain.order.entity;

import com.investory.backend.domain.stock.entity.Stock;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_user_status", columnList = "user_id, status"),
    @Index(name = "idx_order_created_at", columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

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

    /**
     * 체결 시도가 실패했을 때(잔액 부족 등) 주문을 거부 상태로 종료한다.
     * <p>
     * CANCELLED 와 구분하는 이유: CANCELLED 는 사용자의 의사, REJECTED 는 시스템 판단이다.
     * 이력 분석 시 "내가 취소한 주문"과 "잔액이 모자라 튕긴 주문"을 섞으면 안 된다.
     */
    public void reject(String reason) {
        this.status = OrderStatus.REJECTED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelReason = reason;
    }

    /** 아직 체결을 기다리는 상태인지 여부. */
    public boolean isPending() {
        return this.status == OrderStatus.PENDING;
    }

    /**
     * 지정가 주문이 현재가 기준으로 체결 조건을 만족하는지 판단한다.
     * <p>
     * 매수는 시장이 지정가 이하로 내려와야, 매도는 지정가 이상으로 올라와야 체결된다.
     * 이 판정 로직을 매처가 아니라 엔티티에 두면 매처 없이도 단위 테스트가 가능하다.
     */
    public boolean isMatchable(BigDecimal currentPrice) {
        if (this.orderType != OrderType.LIMIT || this.price == null || currentPrice == null) {
            return false;
        }
        return this.side == OrderSide.BUY
                ? currentPrice.compareTo(this.price) <= 0
                : currentPrice.compareTo(this.price) >= 0;
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
