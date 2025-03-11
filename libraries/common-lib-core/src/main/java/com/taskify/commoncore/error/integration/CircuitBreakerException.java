package com.taskify.commoncore.error.integration;

import com.taskify.commoncore.error.CommonErrorCode;
import com.taskify.commoncore.error.exception.IntegrationException;

public class CircuitBreakerException extends IntegrationException {
    public CircuitBreakerException(String message) {
        super(message, CommonErrorCode.CIRCUIT_OPEN);
    }
}