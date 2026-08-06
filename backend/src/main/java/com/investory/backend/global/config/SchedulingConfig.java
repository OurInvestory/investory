package com.investory.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 스케줄러 설정.
 * <p>
 * 메인 클래스가 아닌 별도 설정으로 뺀 이유: 통합 테스트에서 시세 시뮬레이터가 제멋대로 돌면
 * 지정가 체결 검증이 불가능해진다. 테스트 프로파일에서는
 * {@code investory.stock.simulator.enabled=false} 로 스케줄 대상 빈 자체를 등록하지 않는다.
 * <p>
 * <b>왜 스케줄러 풀을 직접 정의했는가</b><br>
 * 스프링 기본 {@code TaskScheduler} 는 스레드 1개다. 시세 갱신 하나만 있을 때는 문제없지만,
 * 나중에 스케줄 작업이 하나만 더 늘어도 서로 실행을 막는다.
 * 지금 명시해 두면 나중에 원인 모를 지연을 디버깅할 일이 없다.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("investory-sched-");
        // 종료 시 진행 중인 시세 갱신 트랜잭션이 잘려서 커넥션이 새지 않도록 대기시킨다.
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(10);
        return scheduler;
    }
}
