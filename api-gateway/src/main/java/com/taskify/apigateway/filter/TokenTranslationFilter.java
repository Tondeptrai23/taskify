package com.taskify.apigateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskify.apigateway.client.AuthClient;
import com.taskify.apigateway.data.TokenVerificationResponse;
import com.taskify.apigateway.exception.ApiGatewayException;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// This filter is used to translate jwt from the request before forwarding to other resource services
@Component
@Slf4j
public class TokenTranslationFilter implements GatewayFilter {
    private final AuthClient authClient;
    private final ReactiveCircuitBreakerFactory circuitBreakerFactory;
    private final ObjectMapper objectMapper;

    @Autowired
    public TokenTranslationFilter(AuthClient authClient,
                                  ReactiveCircuitBreakerFactory circuitBreakerFactory,
                                  ObjectMapper objectMapper) {
        this.authClient = authClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.objectMapper = objectMapper;
    }

    @Override
    @LoggingException
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null) {
            throw new ApiGatewayException("Authorization header is missing", HttpStatus.UNAUTHORIZED);
        }

        if (!authHeader.startsWith("Bearer ")) {
            throw new ApiGatewayException("Authorization header is not a Bearer token", HttpStatus.UNAUTHORIZED);
        }

        // Call Auth Service to verify token - now handling the ApiResponse wrapper
        return authClient.getWebClient()
                .get()
                .uri("/internal/validate")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(response -> {
                    if (response.getData() == null) {
                        return null;
                    }
                    return objectMapper.convertValue(response.getData(), TokenVerificationResponse.class);
                })
                .transform(mono -> circuitBreakerFactory.create("token-validation")
                        .run(mono, this::handleAuthServiceFailure))
                .flatMap(response -> {
                    // Create new request with X-User-Id header
                    ServerHttpRequest modifiedRequest = request.mutate()
                            .headers(headers -> {
                                headers.remove(HttpHeaders.AUTHORIZATION);  // Remove JWT
                                headers.add("X-User-Id", response.getId());  // Add internal user id
                            })
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                });
    }

    private Mono<TokenVerificationResponse> handleAuthServiceFailure(Throwable error) {
        // Log the error
        log.error("Auth service failure in circuit breaker", error);

        // Handle error responses in the ApiResponse format
        if (error instanceof WebClientResponseException) {
            return Mono.error(new ApiGatewayException(error.getMessage(),
                    HttpStatus.valueOf(((WebClientResponseException) error).getStatusCode().value())));
        }

        // For any other failure, return a 503 Service Unavailable
        return Mono.error(new ApiGatewayException(
                "Authentication service unavailable, please try again later",
                HttpStatus.SERVICE_UNAVAILABLE));
    }
}