package com.taskify.apigateway.filter;

import com.taskify.apigateway.exception.ApiGatewayException;
import com.taskify.commoncore.annotation.LoggingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
public class OrgContextValidationFilter implements GatewayFilter {
    @Override
    @LoggingException
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var orgContextHeader = exchange.getRequest().getHeaders().get("X-Organization-Context");
        // If X-Organization-Context header is not present, return 400 Bad Request
        if (orgContextHeader == null) {
            throw new ApiGatewayException("X-Organization-Context header is missing", HttpStatus.BAD_REQUEST);
        }

        // If X-Organization-Context header doesn't have UUID format, return 400 Bad Request
        try {
            UUID.fromString(orgContextHeader.get(0));
        } catch (IllegalArgumentException e) {
            throw new ApiGatewayException("X-Organization-Context header is not a valid UUID", HttpStatus.BAD_REQUEST);
        }

        return chain.filter(exchange);
    }
}
