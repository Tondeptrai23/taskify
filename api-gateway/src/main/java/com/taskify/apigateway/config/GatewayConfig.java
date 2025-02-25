package com.taskify.apigateway.config;

import com.taskify.apigateway.filter.OrgContextValidationFilter;
import com.taskify.apigateway.filter.TokenTranslationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    private final TokenTranslationFilter _tokenTranslationFilter;
    private final OrgContextValidationFilter _orgContextFilter;

    @Autowired
    public GatewayConfig(TokenTranslationFilter tokenTranslationFilter,
                         OrgContextValidationFilter orgContextValidationFilter) {
        _tokenTranslationFilter = tokenTranslationFilter;
        _orgContextFilter = orgContextValidationFilter;
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

                // IAM Service Routes (protected)
                .route("iam-service", r -> r
                        .path("/api/v1/identity", "/api/v1/identity/", "/api/v1/identity/**")
                        .filters(f -> f
                                .filter(_orgContextFilter)
                                .filter(_tokenTranslationFilter)
                                .rewritePath("/api/v1/identity/(?<segment>.*)", "/${segment}"))
                        .uri("lb://iam-service"))

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
