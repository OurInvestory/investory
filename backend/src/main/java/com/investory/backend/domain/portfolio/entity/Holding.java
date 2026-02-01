package com.investory.backend.domain.portfolio.entity;

import com.investory.backend.domain.stock.entity.Stock;
import com.investory.backend.domain.user.entity.User;
import com.investory.backend.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "holdings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "stock_id"})
}, indexes = {
    @Index(name = "idx_holding_user", columnList = "user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Holding extends BaseEntity {

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

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal averagePrice;

    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal totalInvestment;

    public void addQuantity(int qty, BigDecimal price) {
        BigDecimal newInvestment = price.multiply(BigDecimal.valueOf(qty));
        this.totalInvestment = this.totalInvestment.add(newInvestment);
        this.quantity += qty;
        this.averagePrice = this.totalInvestment.divide(BigDecimal.valueOf(this.quantity), 2, java.math.RoundingMode.HALF_UP);
    }

    public void reduceQuantity(int qty) {
        if (this.quantity < qty) {
            throw new IllegalArgumentException("보유 수량보다 많은 수량을 매도할 수 없습니다.");
        }
        BigDecimal soldAmount = this.averagePrice.multiply(BigDecimal.valueOf(qty));
        this.totalInvestment = this.totalInvestment.subtract(soldAmount);
        this.quantity -= qty;
        if (this.quantity == 0) {
            this.averagePrice = BigDecimal.ZERO;
            this.totalInvestment = BigDecimal.ZERO;
        }
    }

    public BigDecimal getCurrentValue(BigDecimal currentPrice) {
        return currentPrice.multiply(BigDecimal.valueOf(this.quantity));
    }

    public BigDecimal getProfitLoss(BigDecimal currentPrice) {
        return getCurrentValue(currentPrice).subtract(this.totalInvestment);
    }

    public BigDecimal getProfitLossRate(BigDecimal currentPrice) {
        if (this.totalInvestment.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getProfitLoss(currentPrice)
                .divide(this.totalInvestment, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
