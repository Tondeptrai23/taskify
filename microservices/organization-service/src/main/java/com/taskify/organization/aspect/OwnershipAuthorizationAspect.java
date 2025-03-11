package com.taskify.organization.aspect;

import com.taskify.commoncore.error.exception.UnauthorizedException;
import com.taskify.organization.annotation.RequiresOwnership;
import com.taskify.organization.service.OrganizationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * Aspect that handles authorization based on organization ownership.
 * Intercepts methods annotated with @RequiresOwnership and verifies
 * the current user is the owner of the organization in the X-Organization-Context header.
 */
@Aspect
@Component
@Slf4j
public class OwnershipAuthorizationAspect {

    private final OrganizationService organizationService;

    @Autowired
    public OwnershipAuthorizationAspect(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    /**
     * Before advice that checks if the user owns the organization they're trying to access.
     * Uses X-Organization-Context and X-User-Id headers from the request.
     *
     * @param joinPoint The join point representing the intercepted method
     * @param requiresOwnership The annotation marker
     */
    @Before("@annotation(requiresOwnership)")
    public void checkOwnership(JoinPoint joinPoint, RequiresOwnership requiresOwnership) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // Get organization ID from context header
        String organizationIdHeader = request.getHeader("X-Organization-Context");
        if (organizationIdHeader == null || organizationIdHeader.isEmpty()) {
            log.error("X-Organization-Context header is missing");
            throw new UnauthorizedException("Organization context is required");
        }

        // Get user ID from header
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader == null || userIdHeader.isEmpty()) {
            log.error("X-User-Id header is missing");
            throw new UnauthorizedException("User ID is required");
        }

        UUID organizationId;
        UUID userId;

        try {
            organizationId = UUID.fromString(organizationIdHeader);
            userId = UUID.fromString(userIdHeader);
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format in headers: {}", e.getMessage());
            throw new UnauthorizedException("Invalid organization or user ID format");
        }

        log.debug("Checking ownership for user {} on organization {}", userId, organizationId);

        // Check ownership through service
        if (!organizationService.isOwner(organizationId, userId)) {
            log.warn("Unauthorized access attempt: User {} is not the owner of organization {}", userId, organizationId);
            throw new UnauthorizedException("You do not have permission to perform this action");
        }

        log.debug("Ownership verification passed for user {} and organization {}", userId, organizationId);
    }
}