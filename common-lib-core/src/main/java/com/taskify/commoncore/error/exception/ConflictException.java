package com.taskify.commoncore.error.exception;

import com.taskify.commoncore.error.CommonErrorCode;
import com.taskify.commoncore.error.ErrorCode;

public class ConflictException extends TaskifyException {
    public ConflictException(String message) {
        super(message, CommonErrorCode.CONFLICT);
    }

    public ConflictException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ConflictException(String message, ErrorCode errorCode, Object details) {
        super(message, errorCode, details);
    }
}