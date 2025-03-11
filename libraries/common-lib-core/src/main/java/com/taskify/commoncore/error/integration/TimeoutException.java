package com.taskify.commoncore.error.integration;

import com.taskify.commoncore.error.CommonErrorCode;
import com.taskify.commoncore.error.exception.IntegrationException;

public class TimeoutException extends IntegrationException {
    public TimeoutException(String message) {
        super(message, CommonErrorCode.TIMEOUT_ERROR);
    }
}