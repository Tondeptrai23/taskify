package com.taskify.common.error.integration;

import com.taskify.common.error.ErrorCode;
import com.taskify.common.error.exception.IntegrationException;

public class CircuitBreakerException extends IntegrationException {
    public CircuitBreakerException(String message) {
        super(message, ErrorCode.CIRCUIT_OPEN);
    }
}