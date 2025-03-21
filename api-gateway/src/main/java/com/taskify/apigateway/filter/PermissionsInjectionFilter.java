package com.taskify.apigateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskify.apigateway.client.IamClient;
import com.taskify.apigateway.data.PermissionsResponse;
import com.taskify.apigateway.exception.ApiGatewayException;
import com.taskify.commoncore.annotation.LoggingAfter;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gateway filter that injects permissions into the request headers.
 * This filter intercepts incoming requests and calls the IAM service to get the permissions
 * Must be called after the TokenTranslationFilter
 */
@Component
@Slf4j
public class PermissionsInjectionFilter implements GatewayFilter {
    private final IamClient iamClient;
    private final ReactiveCircuitBreakerFactory circuitBreakerFactory;
    private final ObjectMapper objectMapper;

    @Autowired
    public PermissionsInjectionFilter(IamClient iamClient,
                                      ReactiveCircuitBreakerFactory circuitBreakerFactory,
                                      ObjectMapper objectMapper) {
        this.iamClient = iamClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.objectMapper = objectMapper;
    }

    @Override
    @LoggingException
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String orgContextHeader = request.getHeaders().getFirst("X-Organization-Context");
        String userIdHeader = request.getHeaders().getFirst("X-User-Id");

        // Validate required headers
        if (orgContextHeader == null) {
            throw new ApiGatewayException("X-Organization-Context header is missing", HttpStatus.BAD_REQUEST);
        }

        if (userIdHeader == null) {
            throw new ApiGatewayException("X-User-Id header is missing", HttpStatus.BAD_REQUEST);
        }

        // Call IAM Service to get permissions
        return iamClient.getWebClient()
                .get()
                .uri("/internal/context/{organizationId}/permissions", orgContextHeader)
                .header("X-Organization-Context", orgContextHeader)
                .header("X-User-Id", userIdHeader)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(response -> {
                    if (response.getData() == null) {
                        return null;
                    }
                    return objectMapper.convertValue(response.getData(), PermissionsResponse.class);
                })
                .transform(mono -> circuitBreakerFactory.create("permissions-retrieval")
                        .run(mono, this::handleIamServiceFailure))
                .flatMap(response -> {
                    // Create new request with X-Permissions header
                    String permissionsHeader = String.join(",", response.getPermissions());
                    log.info("[FILTER] Injecting permissions: {}", permissionsHeader);
                    ServerHttpRequest modifiedRequest = request.mutate()
                            .header("X-Permissions", permissionsHeader)
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                });
    }

    private Mono<PermissionsResponse> handleIamServiceFailure(Throwable error) {
        // Log the error
        log.error("IAM service failure in circuit breaker", error);

        // Handle error responses in the ApiResponse format
        if (error instanceof WebClientResponseException) {
            return Mono.error(new ApiGatewayException(error.getMessage(),
                    HttpStatus.valueOf(((WebClientResponseException) error).getStatusCode().value())));
        }

        // For any other failure, return a 503 Service Unavailable
        return Mono.error(new ApiGatewayException(
                "IAM service unavailable, please try again later",
                HttpStatus.SERVICE_UNAVAILABLE));
    }
}