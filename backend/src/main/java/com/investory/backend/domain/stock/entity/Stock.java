package com.investory.backend.domain.stock.entity;

import com.investory.backend.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "stocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Stock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String englishName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Market market;

    @Column(length = 50)
    private String sector;

    @Column(nullable = false, precision = 20, scale = 2)
    @Builder.Default
    private BigDecimal currentPrice = BigDecimal.ZERO;

    @Column(precision = 20, scale = 2)
    @Builder.Default
    private BigDecimal previousClose = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal changeRate = BigDecimal.ZERO;

    @Column(precision = 20, scale = 2)
    @Builder.Default
    private BigDecimal changeAmount = BigDecimal.ZERO;

    @Column(precision = 20, scale = 2)
    @Builder.Default
    private BigDecimal high52Week = BigDecimal.ZERO;

    @Column(precision = 20, scale = 2)
    @Builder.Default
    private BigDecimal low52Week = BigDecimal.ZERO;

    @Column
    @Builder.Default
    private Long volume = 0L;

    @Column
    @Builder.Default
    private Long marketCap = 0L;

    @Column(length = 500)
    private String logoUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    public void updatePrice(BigDecimal newPrice) {
        this.changeAmount = newPrice.subtract(this.currentPrice);
        if (this.currentPrice.compareTo(BigDecimal.ZERO) > 0) {
            this.changeRate = this.changeAmount.divide(this.currentPrice, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        this.previousClose = this.currentPrice;
        this.currentPrice = newPrice;
    }

    public enum Market {
        KOSPI, KOSDAQ, NASDAQ, NYSE, AMEX
    }
}
