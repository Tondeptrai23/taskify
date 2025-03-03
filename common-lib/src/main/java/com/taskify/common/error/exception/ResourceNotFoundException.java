package com.taskify.common.error.exception;

import com.taskify.common.error.CommonErrorCode;
import com.taskify.common.error.ErrorCode;

public class ResourceNotFoundException extends TaskifyException {
    public ResourceNotFoundException(String message) {
        super(message, CommonErrorCode.RESOURCE_NOT_FOUND);
    }

    public ResourceNotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
