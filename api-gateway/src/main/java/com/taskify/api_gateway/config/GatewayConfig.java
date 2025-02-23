package com.taskify.api_gateway.config;

import com.taskify.api_gateway.filter.TokenTranslationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    private final TokenTranslationFilter _tokenTranslationFilter;

    @Autowired
    public GatewayConfig(TokenTranslationFilter tokenTranslationFilter) {
        _tokenTranslationFilter = tokenTranslationFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service Routes (login, register, etc.)
                .route("auth-service-public", r -> r
                        .path("/api/v1/auth/login", "/api/v1/auth/register", "/api/v1/auth/refresh")
                        .filters(f -> f
                                .rewritePath("/api/v1/auth/(?<segment>.*)", "/auth/${segment}"))
                        .uri("lb://auth-service"))

                // Organization Service Routes (protected)
                .route("organization-service", r -> r
                        .path("/api/v1/orgs", "/api/v1/orgs/", "/api/v1/orgs/**")
                    .filters(f -> f
                                .filter(_tokenTranslationFilter)
                                .rewritePath("/api/v1/(?<segment>.*)", "/${segment}"))
                        .uri("lb://organization-service"))
                .build();
    }
}
