package com.taskify.organization.exception;

public class OrganizationNotFoundException extends ResourceNotFoundException {
    public OrganizationNotFoundException(String message) {
        super(message, "ORGANIZATION_NOT_FOUND");
    }
}
