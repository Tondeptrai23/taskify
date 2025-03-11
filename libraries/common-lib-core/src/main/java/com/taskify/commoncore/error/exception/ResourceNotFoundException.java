package com.taskify.commoncore.error.exception;

import com.taskify.commoncore.error.CommonErrorCode;
import com.taskify.commoncore.error.ErrorCode;

public class ResourceNotFoundException extends TaskifyException {
    public ResourceNotFoundException(String message) {
        super(message, CommonErrorCode.RESOURCE_NOT_FOUND);
    }

    public ResourceNotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
