package com.taskify.common.error.integration;

import com.taskify.common.error.ErrorCode;
import com.taskify.common.error.exception.IntegrationException;

public class TimeoutException extends IntegrationException {
    public TimeoutException(String message) {
        super(message, ErrorCode.TIMEOUT_ERROR);
    }
}