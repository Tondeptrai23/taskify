package com.taskify.iam.service.permission;

import com.taskify.iam.entity.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PermissionVerificationService {
    private final PermissionService permissionService;

    @Autowired
    public PermissionVerificationService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public boolean hasPermission(UUID userId, UUID contextId, String permission) {
        List<Permission> permissions = permissionService.getUserPermissionsInContext(contextId, userId);
        return permissions.stream()
                .anyMatch(p -> p.getName().equals(permission));
    }
}