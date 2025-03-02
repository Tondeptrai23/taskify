package com.taskify.organization.config;

import com.taskify.common.error.ServiceIntegrationException;
import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Resilience4jConfig {
    private final String IAM_SERVICE;

    public Resilience4jConfig(@Value("${services.iam.name}") String iamService) {
        this.IAM_SERVICE = iamService;
    }
    @Bean
    public CircuitBreakerConfigCustomizer iamServiceCircuitBreakerConfig() {
        return CircuitBreakerConfigCustomizer.of(IAM_SERVICE, builder ->
                builder.recordExceptions(ServiceIntegrationException.class)
        );
    }
}