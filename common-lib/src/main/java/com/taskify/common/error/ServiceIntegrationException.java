package com.taskify.common.error;

public class ServiceIntegrationException extends BusinessException {
    public ServiceIntegrationException(String message, String errorCode) {
        super(message, errorCode);
    }

    public ServiceIntegrationException(String message, Throwable cause) {
        super(message, "SERVICE_INTEGRATION_ERROR");
        initCause(cause);
    }

    public ServiceIntegrationException(String message, String errorCode, Throwable cause) {
        super(message, errorCode);
        initCause(cause);
    }
}
