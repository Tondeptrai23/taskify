package com.taskify.apigateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskify.apigateway.data.ErrorResponse;
import com.taskify.apigateway.exception.ApiGatewayException;
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
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalExceptionFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
                .onErrorResume(throwable -> handleError(exchange, throwable));
    }

    private Mono<Void> handleError(ServerWebExchange exchange, Throwable throwable) {
        ServerHttpRequest request = exchange.getRequest();
        HttpStatus status;
        String errorMessage;

        if (throwable instanceof ApiGatewayException) {
            ApiGatewayException ex = (ApiGatewayException) throwable;
            status = ex.getStatus();
            errorMessage = ex.getMessage();
        } else if (throwable instanceof ResponseStatusException) {
            ResponseStatusException ex = (ResponseStatusException) throwable;
            status = HttpStatus.valueOf(ex.getStatusCode().value());
            errorMessage = ex.getReason();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorMessage = "An unexpected error occurred";
            log.error("Unhandled exception in gateway", throwable);
        }

        return renderErrorResponse(exchange, request, status, errorMessage);
    }

    private Mono<Void> renderErrorResponse(
            ServerWebExchange exchange,
            ServerHttpRequest request,
            HttpStatus status,
            String errorMessage
    ) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .path(request.getURI().getPath())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();

        try {
            byte[] responseBytes = objectMapper.writeValueAsString(errorResponse)
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
        // Set to highest precedence to ensure this filter catches all errors
        return Ordered.HIGHEST_PRECEDENCE;
    }
}