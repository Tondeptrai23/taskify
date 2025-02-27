package com.taskify.iam.exception;

public class OrganizationNotFoundException extends ResourceNotFoundException{
    public OrganizationNotFoundException(String message) {
        super(message, "ORG_NOT_FOUND");
    }
}
