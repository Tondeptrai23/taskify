package com.taskify.auth.application.exception;

import com.taskify.auth.domain.exception.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DomainExceptionTranslator {
    private static final Map<Class<? extends AuthDomainException>, AuthErrorCode> ERROR_MAPPINGS = Map.of(
            InvalidCredentialsException.class, AuthErrorCode.INVALID_CREDENTIALS,
            TokenExpiredException.class, AuthErrorCode.TOKEN_EXPIRED,
            TokenRevokedException.class, AuthErrorCode.TOKEN_REVOKED,
            TokenValidationException.class, AuthErrorCode.TOKEN_INVALID
    );

    public AuthApplicationException translate(AuthDomainException exception) {
        AuthErrorCode errorCode = ERROR_MAPPINGS.getOrDefault(
                exception.getClass(),
                AuthErrorCode.UNKNOWN
        );

        return new AuthApplicationException(exception.getMessage(), errorCode);
    }
}