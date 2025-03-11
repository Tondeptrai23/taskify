package com.taskify.apigateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskify.apigateway.exception.ApiGatewayException;
import com.taskify.commoncore.dto.ApiError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalExceptionFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper;
    private static final String SERVICE_NAME = "api-gateway";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
                .onErrorResume(throwable -> handleError(exchange, throwable));
    }

    private Mono<Void> handleError(ServerWebExchange exchange, Throwable throwable) {
        ServerHttpRequest request = exchange.getRequest();
        HttpStatus status;
        String errorMessage;
        String errorCode;

        if (throwable instanceof ApiGatewayException) {
            ApiGatewayException ex = (ApiGatewayException) throwable;
            status = ex.getStatus();
            errorMessage = ex.getMessage();
            errorCode = "API_GATEWAY_" + status.value();
        } else if (throwable instanceof ResponseStatusException) {
            ResponseStatusException ex = (ResponseStatusException) throwable;
            status = HttpStatus.valueOf(ex.getStatusCode().value());
            errorMessage = ex.getReason();
            errorCode = "API_GATEWAY_" + status.value();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorMessage = "An unexpected error occurred";
            errorCode = "API_GATEWAY_INTERNAL_ERROR";
            log.error("Unhandled exception in gateway", throwable);
        }

        return renderErrorResponse(exchange, request, status, errorCode, errorMessage);
    }

    private Mono<Void> renderErrorResponse(
            ServerWebExchange exchange,
            ServerHttpRequest request,
            HttpStatus status,
            String errorCode,
            String errorMessage
    ) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiError apiError = ApiError.builder()
                .success(false)
                .status(status.value())
                .code(errorCode)
                .message(errorMessage)
                .path(request.getURI().getPath())
                .service(SERVICE_NAME)
                .timestamp(ZonedDateTime.now())
                .details(null) // Add details if available
                .build();

        try {
            byte[] responseBytes = objectMapper.writeValueAsString(apiError)
                    .getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(responseBytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Error serializing error response", e);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        // Set to the highest precedence to ensure this filter catches all errors
        return Ordered.HIGHEST_PRECEDENCE;
    }
}