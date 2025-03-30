package com.taskify.auth.domain.exception;

public class TokenValidationException extends AuthDomainException {
    public TokenValidationException(String message) {
        super(message);
    }
}
