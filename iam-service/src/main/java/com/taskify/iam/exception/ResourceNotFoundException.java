package com.taskify.iam.exception;

import com.taskify.common.error.BusinessException;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
}
