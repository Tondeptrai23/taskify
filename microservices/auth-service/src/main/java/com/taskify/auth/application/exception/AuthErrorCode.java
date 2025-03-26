package com.taskify.auth.application.exception;

public enum AuthErrorCode {
    INVALID_CREDENTIALS("AUTH_INVALID_CREDENTIALS"),
    USERNAME_EXISTS("AUTH_USERNAME_EXISTS"),
    EMAIL_EXISTS("AUTH_EMAIL_EXISTS"),
    UNKNOWN("AUTH_UNKNOWN");

    private final String code;

    AuthErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
