package com.taskify.apigateway.config;

import com.taskify.apigateway.filter.OrgContextValidationFilter;
import com.taskify.apigateway.filter.PermissionsInjectionFilter;
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
    private final PermissionsInjectionFilter _permissionsInjectionFilter;

    @Autowired
    public GatewayConfig(TokenTranslationFilter tokenTranslationFilter,
                         OrgContextValidationFilter orgContextValidationFilter,
                         PermissionsInjectionFilter permissionsInjectionFilter) {
        _tokenTranslationFilter = tokenTranslationFilter;
        _orgContextFilter = orgContextValidationFilter;
        _permissionsInjectionFilter = permissionsInjectionFilter;
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

                // Auth Service - User-level routes
                .route("auth-service-user-routes", r -> r
                        .path("/api/v1/auth/users/**")
                        .filters(f -> f
//                                .filter(_tokenTranslationFilter)
                                .rewritePath("/api/v1/auth/users/(?<segment>.*)", "/users/${segment}"))
                        .uri("lb://auth-service"))

                // IAM Service routes
                .route("iam-context-routes", r -> r
                        .path("/api/v1/identity/(orgs|projects)/**") // Match both orgs and projects
                        .filters(f -> f
                                .filter(_orgContextFilter)
                                .filter(_tokenTranslationFilter)
                                .rewritePath("/api/v1/identity/(?<type>orgs|projects)/(?<segment>.*)", "/context/${segment}"))
                        .uri("lb://iam-service"))

                // Organization Service Routes (protected)
                .route("organization-service-membership", r -> r
                        .path("/api/v1/members", "/api/v1/members/**")
                        .filters(f -> f
                                .filter(_orgContextFilter)
                                .filter(_tokenTranslationFilter)
                                .filter(_permissionsInjectionFilter)
                                .rewritePath("/api/v1/(?<segment>.*)", "/${segment}"))
                        .uri("lb://organization-service"))

                // Organization Service - Other routes (without permissions injection)
                .route("organization-service-other", r -> r
                        .path("/api/v1/orgs", "/api/v1/orgs/", "/api/v1/orgs/**")
                        .and().not(p -> p.path("/api/v1/orgs/*/members", "/api/v1/orgs/*/members/**"))
                        .filters(f -> f
                                .filter(_orgContextFilter)
                                .filter(_tokenTranslationFilter)
                                .rewritePath("/api/v1/(?<segment>.*)", "/${segment}"))
                        .uri("lb://organization-service"))

                // Other routes
                .build();
    }
}
