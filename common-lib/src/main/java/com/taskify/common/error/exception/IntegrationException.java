package com.taskify.common.error.exception;

import com.taskify.common.error.CommonErrorCode;
import com.taskify.common.error.ErrorCode;

public class IntegrationException extends TaskifyException {
    public IntegrationException(String message) {
        super(message, CommonErrorCode.INTEGRATION_ERROR);
    }

    public IntegrationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public IntegrationException(String message, Throwable cause) {
        super(message, CommonErrorCode.INTEGRATION_ERROR, cause);
    }
}