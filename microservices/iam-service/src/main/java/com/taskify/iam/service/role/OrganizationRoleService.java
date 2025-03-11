package com.taskify.iam.service.role;

import com.taskify.commoncore.error.resource.OrganizationNotFoundException;
import com.taskify.commoncore.error.resource.RoleNotFoundException;
import com.taskify.iam.dto.role.CreateOrganizationRoleDto;
import com.taskify.iam.entity.LocalOrganization;
import com.taskify.iam.entity.Role;
import com.taskify.iam.mapper.OrganizationRoleMapper;
import com.taskify.iam.repository.OrganizationRepository;
import com.taskify.iam.repository.OrganizationRoleRepository;
import com.taskify.iam.service.permission.PermissionPrerequisiteValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class OrganizationRoleService extends BaseRoleService<CreateOrganizationRoleDto> {
    private final OrganizationRoleRepository _orgRoleRepository;
    private final OrganizationRepository _organizationRepository;
    private final OrganizationRoleMapper _roleMapper;

    @Autowired
    public OrganizationRoleService(
            OrganizationRoleRepository orgRoleRepository,
            OrganizationRepository organizationRepository,
            OrganizationRoleMapper roleMapper,
            PermissionPrerequisiteValidator permissionPrerequisiteValidator) {
        super(permissionPrerequisiteValidator);
        this._orgRoleRepository = orgRoleRepository;
        this._organizationRepository = organizationRepository;
        this._roleMapper = roleMapper;
    }

    @Override
    protected Optional<Role> findRoleByIdWithPermissions(UUID roleId, UUID contextId) {
        return _orgRoleRepository.findRoleByIdAndOrgIdWithPermissions(roleId, contextId);
    }

    @Override
    protected Optional<Role> findRoleById(UUID roleId, UUID contextId) {
        return _orgRoleRepository.findRoleByIdAndOrgId(roleId, contextId);
    }

    @Override
    protected List<Role> findAllRolesWithPermissions(UUID contextId) {
        return _orgRoleRepository.findAllWithPermissionsInOrg(contextId);
    }

    @Override
    protected Optional<Role> findDefaultRoleByContextId(UUID contextId) {
        return _orgRoleRepository.findDefaultRoleByOrgId(contextId);
    }

    @Override
    protected void validateContext(UUID contextId) {
        _organizationRepository.findById(contextId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));
    }

    @Override
    protected Role mapDtoToEntity(CreateOrganizationRoleDto createRoleDto) {
        return _roleMapper.toEntity(createRoleDto);
    }

    @Override
    protected void updateRoleFields(Role existingRole, CreateOrganizationRoleDto roleDto) {
        existingRole.setName(roleDto.getName());
        existingRole.setDescription(roleDto.getDescription());
    }

    @Override
    protected void setRoleContext(Role role, UUID contextId) {
        LocalOrganization organization = _organizationRepository.findById(contextId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));
        role.setOrganization(organization);
    }

    @Override
    protected List<String> getRolePermissions(CreateOrganizationRoleDto createRoleDto) {
        return createRoleDto.getPermissions();
    }

    @Override
    protected Role saveRole(Role role) {
        return _orgRoleRepository.save(role);
    }

    @Override
    protected void deleteRoleFromRepository(Role role) {
        _orgRoleRepository.delete(role);
    }

    @Override
    protected RoleNotFoundException createRoleNotFoundException(String message) {
        return new RoleNotFoundException(message);
    }
}