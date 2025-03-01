package com.taskify.common.error;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED");
    }

    public UnauthorizedException(String message, String errorCode) {
        super(message, errorCode);
    }
}
