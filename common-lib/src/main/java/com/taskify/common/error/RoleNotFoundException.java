package com.taskify.common.error;

public class RoleNotFoundException extends ResourceNotFoundException {
    public RoleNotFoundException(String message) {
        super(message, "ROLE_NOT_FOUND");
    }
}
