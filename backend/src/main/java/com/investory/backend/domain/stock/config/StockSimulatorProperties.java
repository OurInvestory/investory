package com.investory.backend.domain.stock.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 시세 시뮬레이터 설정.
 * <p>
 * 갱신 주기와 변동성을 코드에 박지 않은 이유: 테스트에서는 스케줄러를 꺼야 하고,
 * 데모에서는 빠르게(1초), 운영 데모에서는 느리게(10초) 돌리고 싶기 때문이다.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "investory.stock.simulator")
public class StockSimulatorProperties {

    /** 시뮬레이터 활성화 여부. 테스트 프로파일에서는 false. */
    private boolean enabled = true;

    /** 갱신 주기(ms). fixedDelay 기준이라 이전 실행이 끝난 뒤부터 센다. */
    private long intervalMs = 5000L;

    /** 틱당 최대 변동률 (0.005 = ±0.5%). */
    private double volatility = 0.005d;

    /**
     * 난수 시드. null 이면 매 실행마다 다른 시세가 나오고,
     * 값을 지정하면 재현 가능한 시세 시퀀스가 만들어진다(테스트/디버깅용).
     */
    private Long seed;

    /** 틱당 추가되는 거래량의 상한. */
    private long maxVolumePerTick = 100_000L;
}
