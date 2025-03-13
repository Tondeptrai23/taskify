package com.taskify.organization.aspect;

import com.taskify.commoncore.error.exception.UnauthorizedException;
import com.taskify.organization.annotation.RequiresPermissions;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Aspect
@Component
@Slf4j
public class PermissionsAuthorizationAspect {
    @Before("@annotation(requiresPermissions)")
    public void checkPermissions(JoinPoint joinPoint, RequiresPermissions requiresPermissions) {
        log.info("Checking permissions for method: {}", joinPoint.getSignature().getName());

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        var permissions = request.getHeader("X-Permissions");
        if (permissions == null || permissions.isEmpty()) {
            log.error("X-Permissions header is missing");
            throw new UnauthorizedException("Permissions are required");
        }

        log.info("Permissions: {}", permissions);

        // Check if the user has the required permissions
        var permissionsArray = permissions.split(",");
        Set<String> userPermissions = new HashSet<>(Arrays.asList(permissionsArray));
        for (var requiredPermission : requiresPermissions.value()) {
            if (userPermissions.contains(requiredPermission.getValue())) {
                log.info("User has the required permissions");
                return;
            }
        }

        log.error("User does not have the required permissions");
        throw new UnauthorizedException("User does not have the required permissions");
    }
}
