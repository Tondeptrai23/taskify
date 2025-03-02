package com.taskify.common.error;

public class CircuitBreakerOpenedException extends ServiceIntegrationException {
    public CircuitBreakerOpenedException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }

    public CircuitBreakerOpenedException(String message, Throwable cause) {
        super(message, "CIRCUIT_BREAKER", cause);
    }

    public CircuitBreakerOpenedException(Throwable cause) {
        super("Circuit breaker is open", "CIRCUIT_BREAKER", cause);
    }
}
