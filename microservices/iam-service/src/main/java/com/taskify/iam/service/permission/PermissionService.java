package com.taskify.iam.service.permission;

import com.taskify.iam.entity.Context;
import com.taskify.iam.entity.Permission;
import com.taskify.iam.entity.PermissionGroup;
import com.taskify.iam.repository.ContextRepository;
import com.taskify.iam.repository.PermissionGroupRepository;
import com.taskify.iam.repository.PermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionGroupRepository permissionGroupRepository;
    private final ContextRepository contextRepository;

    @Autowired
    public PermissionService(
            PermissionRepository permissionRepository,
            PermissionGroupRepository permissionGroupRepository,
            ContextRepository contextRepository) {
        this.permissionRepository = permissionRepository;
        this.permissionGroupRepository = permissionGroupRepository;
        this.contextRepository = contextRepository;
    }

    public List<Permission> getPermissions(Long groupId) {
        if (groupId != null) {
            return permissionRepository.findPermissionsByGroupId(groupId);
        }
        return permissionRepository.findAllPermissions();
    }

    public List<PermissionGroup> getPermissionGroups() {
        return permissionGroupRepository.findAllWithPermissions();
    }

    public List<Permission> getUserPermissionsInContext(UUID contextId, UUID userId) {
        // Get current context and all ancestors in the hierarchy
        List<Context> contextHierarchy = contextRepository.findContextWithAncestors(contextId);
        List<UUID> contextIds = contextHierarchy.stream()
                .map(Context::getId)
                .collect(Collectors.toList());

        // Get all permissions across the context hierarchy
        return permissionRepository.findUserPermissionsInContexts(userId, contextIds);
    }
}