package com.taskify.auth.application.exception;

public enum AuthErrorCode {
    INVALID_CREDENTIALS("AUTH_INVALID_CREDENTIALS"),
    USERNAME_EXISTS("AUTH_USERNAME_EXISTS"),
    EMAIL_EXISTS("AUTH_EMAIL_EXISTS"),
    USER_NOT_FOUND("AUTH_USER_NOT_FOUND"),
    TOKEN_EXPIRED("AUTH_TOKEN_EXPIRED"),
    TOKEN_REVOKED("AUTH_TOKEN_REVOKED"),
    TOKEN_INVALID("AUTH_TOKEN_INVALID"),
    UNKNOWN("AUTH_UNKNOWN");

    private final String code;

    AuthErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
