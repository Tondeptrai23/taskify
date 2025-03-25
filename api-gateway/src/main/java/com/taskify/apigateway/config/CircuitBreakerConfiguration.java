package com.taskify.apigateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfiguration {
    private static final int SLIDING_WINDOW_SIZE = 10;
    private static final float FAILURE_RATE_THRESHOLD = 50;
    private static final int WAIT_DURATION_IN_OPEN_STATE_SECONDS = 10;
    private static final int PERMITTED_CALLS_IN_HALF_OPEN_STATE = 5;
    private static final float SLOW_CALL_RATE_THRESHOLD = 50;
    private static final int SLOW_CALL_DURATION_THRESHOLD_SECONDS = 2;
    private static final int TIMEOUT_DURATION_SECONDS = 3;

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(SLIDING_WINDOW_SIZE)
                        .failureRateThreshold(FAILURE_RATE_THRESHOLD)
                        .waitDurationInOpenState(Duration.ofSeconds(WAIT_DURATION_IN_OPEN_STATE_SECONDS))
                        .permittedNumberOfCallsInHalfOpenState(PERMITTED_CALLS_IN_HALF_OPEN_STATE)
                        .slowCallRateThreshold(SLOW_CALL_RATE_THRESHOLD)
                        .slowCallDurationThreshold(Duration.ofSeconds(SLOW_CALL_DURATION_THRESHOLD_SECONDS))
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(TIMEOUT_DURATION_SECONDS))
                        .build())
                .build());
    }

}