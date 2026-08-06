package com.investory.backend.domain.stock.service;

import com.investory.backend.domain.stock.config.StockSimulatorProperties;
import com.investory.backend.domain.stock.entity.Stock;
import com.investory.backend.domain.stock.event.StockPriceUpdatedEvent;
import com.investory.backend.domain.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 시세 시뮬레이터.
 * <p>
 * 실제 거래소 시세가 아니라 <b>랜덤워크로 생성한 가상 시세</b>다.
 * 실서비스 연동(한국투자증권 오픈API)은 로드맵 항목이며, 그때 이 컴포넌트는
 * 가격 피드 인터페이스의 한 구현으로 내려가면 된다.
 * <p>
 * <b>왜 fixedRate 가 아니라 fixedDelay 인가</b><br>
 * {@code fixedRate} 는 이전 실행이 끝나지 않아도 다음 실행을 예약한다. 종목 수가 늘어
 * 한 틱이 주기보다 오래 걸리면 실행이 밀려서 누적되고, 결국 DB 커넥션을 다 먹는다.
 * {@code fixedDelay} 는 이전 실행이 끝난 시점부터 다시 세므로 그런 폭주가 없다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "investory.stock.simulator.enabled", havingValue = "true", matchIfMissing = true)
public class StockPriceSimulator {

    private final StockRepository stockRepository;
    private final StockSimulatorProperties properties;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 난수 생성기.
     * 스케줄러 스레드 하나만 접근하므로 동기화가 필요 없다.
     * 시드가 주어지면 재현 가능한 시퀀스가 만들어져 테스트/디버깅이 쉬워진다.
     */
    private Random random;

    private Random random() {
        if (random == null) {
            random = properties.getSeed() != null ? new Random(properties.getSeed()) : new Random();
        }
        return random;
    }

    @Scheduled(
            fixedDelayString = "${investory.stock.simulator.interval-ms:5000}",
            initialDelayString = "${investory.stock.simulator.interval-ms:5000}"
    )
    @Transactional
    public void tick() {
        List<Stock> stocks = stockRepository.findAll();
        if (stocks.isEmpty()) {
            return;
        }

        List<StockPriceUpdatedEvent.PriceTick> ticks = new ArrayList<>(stocks.size());
        for (Stock stock : stocks) {
            BigDecimal newPrice = nextPrice(stock.getCurrentPrice());
            long addedVolume = (long) (random().nextDouble() * properties.getMaxVolumePerTick());

            stock.updatePrice(newPrice, addedVolume);
            ticks.add(StockPriceUpdatedEvent.PriceTick.from(stock));
        }

        // 매칭과 SSE 전송은 시세가 커밋된 뒤에 일어나야 한다.
        // 여기서 매처를 직접 호출하면 아직 커밋 안 된 시세로 체결을 판정하게 되고,
        // 이 트랜잭션이 롤백되면 "존재하지 않는 시세로 체결된 주문"이 남는다.
        eventPublisher.publishEvent(new StockPriceUpdatedEvent(ticks));
        log.debug("시세 갱신 완료: {}종목", ticks.size());
    }

    /**
     * 랜덤워크 한 스텝.
     * 변동률을 {@code [-volatility, +volatility]} 균등분포에서 뽑아 현재가에 곱한다.
     * 가격은 원 단위로 반올림하고, 0 이하로 내려가지 않도록 하한을 1로 둔다.
     */
    private BigDecimal nextPrice(BigDecimal currentPrice) {
        double delta = (random().nextDouble() * 2 - 1) * properties.getVolatility();
        BigDecimal next = currentPrice
                .multiply(BigDecimal.valueOf(1 + delta))
                .setScale(0, RoundingMode.HALF_UP);

        return next.compareTo(BigDecimal.ONE) < 0 ? BigDecimal.ONE : next;
    }
}
