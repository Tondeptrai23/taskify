package com.taskify.auth.exception;

import com.taskify.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum AuthErrorCode  implements ErrorCode {
    EMAIL_EXISTS("AUTH_EMAIL_EXISTS", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS("AUTH_INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED),
    USERNAME_EXISTS("AUTH_USERNAME_EXISTS", HttpStatus.CONFLICT);

    private final String code;
    private final HttpStatus status;

    AuthErrorCode(String code, HttpStatus status) {
        this.code = code;
        this.status = status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
