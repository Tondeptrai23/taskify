package com.taskify.auth.application.exception;

public class UsernameExistsException extends AuthApplicationException{
    public UsernameExistsException(String username) {
        super("Username " + username + " already exists", AuthErrorCode.USERNAME_EXISTS);
    }
}
