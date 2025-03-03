package com.taskify.common.error.exception;

import com.taskify.common.error.ErrorCode;

public class ConflictException extends TaskifyException {
    public ConflictException(String message) {
        super(message, ErrorCode.CONFLICT);
    }

    public ConflictException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ConflictException(String message, ErrorCode errorCode, Object details) {
        super(message, errorCode, details);
    }
}