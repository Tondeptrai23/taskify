package com.taskify.api_gateway.filter;

import com.taskify.api_gateway.data.TokenVerificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// This filter is used to translate jwt from the request before forwarding to other resource services
@Component
@Slf4j
public class TokenTranslationFilter implements GatewayFilter {
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public TokenTranslationFilter(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }

        // Call Auth Service to verify token
        return webClientBuilder.build()
                .get()
                .uri("lb://auth-service/internal/validate")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .bodyToMono(TokenVerificationResponse.class)
                .flatMap(response -> {
                    // Create new request with X-User-Id header
                    ServerHttpRequest modifiedRequest = request.mutate()
                            .headers(headers -> {
                                headers.remove(HttpHeaders.AUTHORIZATION);  // Remove JWT
                                headers.add("X-User-Id", response.getId());  // Add internal user id
                            })
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                })
                .onErrorResume(WebClientResponseException.class, e -> {
                    log.error("Error verifying token", e);
                    if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                        return onError(exchange, HttpStatus.UNAUTHORIZED);
                    }
                    return onError(exchange, HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }
}