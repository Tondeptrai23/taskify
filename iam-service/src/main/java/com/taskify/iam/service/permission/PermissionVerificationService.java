package com.taskify.iam.service.permission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PermissionVerificationService {
    private final PermissionService _permissionService;

    @Autowired
    public PermissionVerificationService(PermissionService permissionService) {
        _permissionService = permissionService;
    }

    public boolean hasPermission(UUID userId, UUID orgId, UUID projectId, String permission) {
        if (projectId == null) {
            return _permissionService.getOrganizationPermissionsOfUser(orgId, userId)
                    .stream()
                    .anyMatch(p -> p.getName().equals(permission));
        } else {
            return _permissionService.getProjectPermissionsOfUser(projectId, userId, orgId)
                    .stream()
                    .anyMatch(p -> p.getName().equals(permission));
        }
    }
}
