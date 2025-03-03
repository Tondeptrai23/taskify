package com.taskify.common.error;

import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements ErrorCode {
    // General errors
    VALIDATION_ERROR("VALIDATION_ERROR", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("UNAUTHORIZED", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("FORBIDDEN", HttpStatus.FORBIDDEN),
    CONFLICT("CONFLICT", HttpStatus.CONFLICT),
    INTERNAL_ERROR("INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),

    // Authentication/Authorization errors
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("TOKEN_INVALID", HttpStatus.UNAUTHORIZED),

    // Resource-specific errors
    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND),
    ORG_NOT_FOUND("ORG_NOT_FOUND", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND("ROLE_NOT_FOUND", HttpStatus.NOT_FOUND),
    PROJECT_NOT_FOUND("PROJECT_NOT_FOUND", HttpStatus.NOT_FOUND),
    USERNAME_EXISTS("USERNAME_EXISTS", HttpStatus.CONFLICT),
    EMAIL_EXISTS("EMAIL_EXISTS", HttpStatus.CONFLICT),

    // Service integration errors
    INTEGRATION_ERROR("INTEGRATION_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE),
    TIMEOUT_ERROR("TIMEOUT_ERROR", HttpStatus.GATEWAY_TIMEOUT),
    CIRCUIT_OPEN("CIRCUIT_OPEN", HttpStatus.SERVICE_UNAVAILABLE),

    // Business logic errors
    DEFAULT_ROLE_DELETION("DEFAULT_ROLE_DELETION", HttpStatus.CONFLICT),
    MISSING_PERMISSION_PREREQUISITE("MISSING_PERMISSION_PREREQUISITE", HttpStatus.CONFLICT);

    private final String code;
    private final HttpStatus status;

    CommonErrorCode(String code, HttpStatus status) {
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}