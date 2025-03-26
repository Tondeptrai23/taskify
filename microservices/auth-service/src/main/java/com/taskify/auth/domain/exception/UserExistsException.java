package com.taskify.auth.domain.exception;

public class UserExistsException extends AuthDomainException {
    public UserExistsException(String message) {
        super(message);
    }
}
