package com.taskify.api_gateway.filter;

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
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var orgContextHeader = exchange.getRequest().getHeaders().get("X-Organization-Context");
        // If X-Organization-Context header is not present, return 400 Bad Request
        if (orgContextHeader == null) {
            log.error("X-Organization-Context header is missing");
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return exchange.getResponse().setComplete();
        }

        // If X-Organization-Context header doesn't have UUID format, return 400 Bad Request
        try {
            UUID.fromString(orgContextHeader.get(0));
        } catch (IllegalArgumentException e) {
            log.error("X-Organization-Context header is not a valid UUID");
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}
