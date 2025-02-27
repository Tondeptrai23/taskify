package com.taskify.iam.service;

import com.taskify.iam.entity.Permission;
import com.taskify.iam.exception.MissingPermissionPrerequisiteException;
import com.taskify.iam.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class PermissionPrerequisiteValidator {
    private final PermissionRepository _permissionRepository;

    @Autowired
    public PermissionPrerequisiteValidator(PermissionRepository permissionRepository) {
        _permissionRepository = permissionRepository;
    }

    public List<Permission> validatePermissionPrerequisites(List<String> permissions) {
        var permissionEntities = _permissionRepository.findPermissionsByNameIn(permissions);

        List<String> prerequisites = permissionEntities.stream()
                .map(permission -> {
                    if (permission.getPrerequisites() != null) {
                        return permission.getPrerequisites();
                    }
                    return List.<String>of();
                })
                .flatMap(List::stream)
                .distinct()
                .toList();

        List<String> listedPermissions = permissionEntities.stream()
                .map(Permission::getName)
                .toList();

        List<String> missingPermissions = prerequisites.stream()
                .filter(prerequisite -> !listedPermissions.contains(prerequisite))
                .toList();

        if (!missingPermissions.isEmpty()) {
            throw new MissingPermissionPrerequisiteException(missingPermissions);
        }

        return permissionEntities;
    }
}
