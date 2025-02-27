package com.taskify.common.error;

public class OrganizationNotFoundException extends ResourceNotFoundException {
    public OrganizationNotFoundException(String message) {
        super(message, "ORG_NOT_FOUND");
    }
}
