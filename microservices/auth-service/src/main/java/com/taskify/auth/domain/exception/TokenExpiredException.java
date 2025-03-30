package com.taskify.auth.domain.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TokenExpiredException extends TokenValidationException {
    private final LocalDateTime expirationTime;

    public TokenExpiredException(String tokenType, LocalDateTime expirationTime) {
        super(String.format("%s token expired at %s", tokenType, expirationTime));
        this.expirationTime = expirationTime;
    }
}