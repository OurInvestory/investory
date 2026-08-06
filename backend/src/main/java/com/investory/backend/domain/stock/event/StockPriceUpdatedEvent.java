package com.investory.backend.domain.stock.event;

import com.investory.backend.domain.stock.entity.Stock;

import java.math.BigDecimal;
import java.util.List;

/**
 * 시세 갱신 완료 이벤트.
 * <p>
 * 시뮬레이터 클래스 내부에 두지 않고 별도 패키지로 뺀 이유:
 * 지정가 매처와 SSE 스트림이 이 이벤트를 구독하는데, 그러려면 시뮬레이터 구현 클래스를
 * import 해야 해서 "구독자가 발행자 구현에 의존"하는 구조가 된다.
 * 나중에 실거래소 피드로 발행자가 바뀌어도 구독자는 그대로여야 한다.
 */
public record StockPriceUpdatedEvent(List<PriceTick> ticks) {

    /** 한 종목의 갱신 결과 스냅샷 (엔티티가 아닌 값). */
    public record PriceTick(
            Long stockId,
            String code,
            String name,
            BigDecimal currentPrice,
            BigDecimal changeAmount,
            BigDecimal changeRate,
            Long volume
    ) {
        public static PriceTick from(Stock stock) {
            return new PriceTick(
                    stock.getId(),
                    stock.getCode(),
                    stock.getName(),
                    stock.getCurrentPrice(),
                    stock.getChangeAmount(),
                    stock.getChangeRate(),
                    stock.getVolume()
            );
        }
    }
}
