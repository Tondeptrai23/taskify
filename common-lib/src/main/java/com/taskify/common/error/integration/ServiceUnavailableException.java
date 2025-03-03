package com.taskify.common.error.integration;

import com.taskify.common.error.ErrorCode;
import com.taskify.common.error.exception.IntegrationException;

public class ServiceUnavailableException extends IntegrationException {
    public ServiceUnavailableException(String message) {
        super(message, ErrorCode.SERVICE_UNAVAILABLE);
    }
}