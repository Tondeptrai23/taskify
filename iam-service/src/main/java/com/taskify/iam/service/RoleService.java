package com.taskify.iam.service;

import com.taskify.iam.dto.role.CreateRoleDto;
import com.taskify.iam.entity.Role;
import com.taskify.iam.mapper.RoleMapper;
import com.taskify.iam.repository.PermissionRepository;
import com.taskify.iam.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class RoleService {
    private final RoleRepository _roleRepository;
    private final PermissionRepository _permissionRepository;
    private final RoleMapper _roleMapper;

    @Autowired
    public RoleService(RoleRepository roleRepository,
                       PermissionRepository permissionRepository,
                       RoleMapper roleMapper) {
        _roleRepository = roleRepository;
        _permissionRepository = permissionRepository;
        _roleMapper = roleMapper;
    }
    public List<Role> getRoles(UUID organizationId) {
        var roles = _roleRepository.findAllWithPermissionsInOrg(organizationId.toString());

        log.info("Roles: {}", roles);

        return roles;
    }
}
