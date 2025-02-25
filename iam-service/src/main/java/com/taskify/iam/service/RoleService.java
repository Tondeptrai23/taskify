package com.taskify.iam.service;

import com.taskify.iam.entity.Role;
import com.taskify.iam.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RoleService {
    private final RoleRepository _roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        _roleRepository = roleRepository;
    }
    public List<Role> getRoles() {
        var roles = _roleRepository.findAllWithPermissions();

        log.info("Roles: {}", roles);

        return roles;
    }
}
