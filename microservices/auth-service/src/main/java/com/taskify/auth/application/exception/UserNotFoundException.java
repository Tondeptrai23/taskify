package com.taskify.auth.application.exception;

public class UserNotFoundException extends AuthApplicationException{
    public UserNotFoundException(String message) {
        super(message, AuthErrorCode.USER_NOT_FOUND);
    }
}
