package com.taskify.iam.service;

import com.taskify.iam.entity.Permission;
import com.taskify.iam.entity.PermissionGroup;
import com.taskify.iam.repository.PermissionGroupRepository;
import com.taskify.iam.repository.PermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PermissionService {
    private final PermissionRepository _permissionRepository;
    private final PermissionGroupRepository _permissionGroupRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository,
                             PermissionGroupRepository permissionGroupRepository) {
        _permissionRepository = permissionRepository;
        _permissionGroupRepository = permissionGroupRepository;
    }

    public List<Permission> getPermissions(Long groupId) {
        if (groupId != null) {
            return _permissionRepository.findPermissionsByGroupId(groupId);
        }

        return _permissionRepository.findAllPermissions();
    }

    public List<PermissionGroup> getPermissionGroups() {
        return _permissionGroupRepository.findAllWithPermissions();
    }
}