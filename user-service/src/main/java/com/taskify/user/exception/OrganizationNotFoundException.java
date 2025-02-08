package com.taskify.user.exception;

public class OrganizationNotFoundException extends ResourceNotFoundException {
    public OrganizationNotFoundException(String message) {
        super(message, "ORGANIZATION_NOT_FOUND");
    }
}
