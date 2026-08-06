package com.investory.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * {@code @Retryable} 애노테이션을 활성화한다.
 * <p>
 * 메인 애플리케이션 클래스에 붙이지 않고 별도 설정 클래스로 뺀 이유:
 * 통합 테스트에서 재시도를 끄고 "원본 예외가 실제로 터지는지"를 확인하고 싶을 때,
 * 이 설정만 교체하거나 제외하면 되기 때문이다.
 */
@Configuration
@EnableRetry
public class RetryConfig {
}
