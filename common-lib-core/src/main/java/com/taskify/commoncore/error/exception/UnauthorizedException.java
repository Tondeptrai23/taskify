package com.taskify.commoncore.error.exception;

import com.taskify.commoncore.error.CommonErrorCode;
import com.taskify.commoncore.error.ErrorCode;

public class UnauthorizedException extends TaskifyException {
    public UnauthorizedException(String message) {
        super(message, CommonErrorCode.UNAUTHORIZED);
    }

    public UnauthorizedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}