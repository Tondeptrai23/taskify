package com.taskify.auth.domain.exception;

public class TokenRevokedException extends TokenValidationException {
    public TokenRevokedException(String message) {
        super(message);
    }
}