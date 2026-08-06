package com.investory.backend.domain.stock.entity;

import com.investory.backend.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "stocks", indexes = {
    @Index(name = "idx_stock_code", columnList = "code"),
    @Index(name = "idx_stock_market", columnList = "market"),
    @Index(name = "idx_stock_sector", columnList = "sector")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Stock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

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

    /**
     * 현재가를 갱신하고 <b>전일 종가 대비</b> 변동폭/변동률을 다시 계산한다.
     * <p>
     * 기존 구현은 갱신 때마다 {@code previousClose = currentPrice} 로 덮어써서, 변동률이
     * "전일 대비"가 아니라 "직전 틱 대비"가 되어 있었다. 시세가 초 단위로 갱신되는 시뮬레이터를
     * 붙이면 변동률이 항상 ±0.5% 언저리에 머물러 화면상 의미가 사라진다.
     * previousClose 는 장 마감/일자 롤오버 시점에만 바뀌어야 하는 값이므로 여기서는 건드리지 않는다.
     *
     * @param newPrice    새 현재가
     * @param addedVolume 이번 틱에서 추가된 거래량 (누적)
     */
    public void updatePrice(BigDecimal newPrice, long addedVolume) {
        this.currentPrice = newPrice;
        this.changeAmount = newPrice.subtract(this.previousClose);

        if (this.previousClose.compareTo(BigDecimal.ZERO) > 0) {
            this.changeRate = this.changeAmount
                    .divide(this.previousClose, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
        }

        this.volume += addedVolume;
        this.high52Week = this.high52Week.max(newPrice);
        this.low52Week = this.low52Week.compareTo(BigDecimal.ZERO) == 0
                ? newPrice
                : this.low52Week.min(newPrice);
    }

    public void updatePrice(BigDecimal newPrice) {
        updatePrice(newPrice, 0L);
    }

    /**
     * 일자 롤오버. 현재가를 전일 종가로 확정하고 변동폭/거래량을 초기화한다.
     * 시뮬레이터가 하루 경계를 넘길 때 호출할 수 있도록 열어 둔다.
     */
    public void rollOverDay() {
        this.previousClose = this.currentPrice;
        this.changeAmount = BigDecimal.ZERO;
        this.changeRate = BigDecimal.ZERO;
        this.volume = 0L;
    }

    public enum Market {
        KOSPI, KOSDAQ, NASDAQ, NYSE, AMEX
    }
}
