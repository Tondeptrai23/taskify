package com.taskify.commoncore.error.integration;

import com.taskify.commoncore.error.CommonErrorCode;
import com.taskify.commoncore.error.exception.IntegrationException;

public class ServiceUnavailableException extends IntegrationException {
    public ServiceUnavailableException(String message) {
        super(message, CommonErrorCode.SERVICE_UNAVAILABLE);
    }
}