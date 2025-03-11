package com.taskify.organization.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods that require organization ownership verification.
 * Used with OwnershipAuthorizationAspect to enforce that the current user
 * is the owner of the organization specified in the X-Organization-Context header.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresOwnership {
    // No parameters needed as we'll use X-Organization-Context header
}