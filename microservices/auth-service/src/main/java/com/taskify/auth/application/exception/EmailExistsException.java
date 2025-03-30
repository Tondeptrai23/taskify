package com.taskify.auth.application.exception;

public class EmailExistsException extends AuthApplicationException{
    public EmailExistsException(String email) {
        super("Email already exists: " + email, AuthErrorCode.EMAIL_EXISTS);
    }
}
