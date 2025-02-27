package com.taskify.common.error;

public class ConflictException extends BusinessException {
    public ConflictException(String message, String errorCode) {
        super(message, errorCode);
    }
}
