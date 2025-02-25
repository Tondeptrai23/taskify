package com.taskify.apigateway.exception;

import org.springframework.http.HttpStatus;

public class ApiGatewayException extends RuntimeException {
    private final HttpStatus status;

    public ApiGatewayException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}