package com.taskify.auth.application.exception;

import com.taskify.auth.domain.exception.AuthDomainException;
import com.taskify.auth.domain.exception.InvalidCredentialsException;
import com.taskify.auth.domain.exception.UserExistsException;
import org.springframework.stereotype.Component;

@Component
public class DomainExceptionTranslator {
    public AuthApplicationException translate(AuthDomainException exception) {
        if (exception instanceof InvalidCredentialsException) {
            return new AuthApplicationException(exception.getMessage(), AuthErrorCode.INVALID_CREDENTIALS);
        }

        if (exception instanceof UserExistsException) {
            return new AuthApplicationException(exception.getMessage(), AuthErrorCode.USERNAME_EXISTS);
        }

        return new AuthApplicationException(exception.getMessage(), AuthErrorCode.UNKNOWN);
    }
}

