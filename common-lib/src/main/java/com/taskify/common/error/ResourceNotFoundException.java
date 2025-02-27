package com.taskify.common.error;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
}
