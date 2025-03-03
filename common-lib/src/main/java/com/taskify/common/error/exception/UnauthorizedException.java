package com.taskify.common.error.exception;

import com.taskify.common.error.CommonErrorCode;
import com.taskify.common.error.ErrorCode;

public class UnauthorizedException extends TaskifyException {
    public UnauthorizedException(String message) {
        super(message, CommonErrorCode.UNAUTHORIZED);
    }

    public UnauthorizedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}