package com.taskify.auth.exception;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message) {
        super(message, "USER_NOT_FOUND");
    }
}
