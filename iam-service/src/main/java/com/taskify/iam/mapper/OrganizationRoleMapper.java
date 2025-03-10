package com.taskify.iam.mapper;

import com.taskify.iam.dto.role.CreateOrganizationRoleDto;
import com.taskify.iam.dto.role.OrganizationRoleDto;
import com.taskify.iam.entity.Permission;
import com.taskify.iam.entity.Role;
import com.taskify.iam.entity.RoleType;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrganizationRoleMapper {
    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "permissionsToStringList")
    @Mapping(target = "organizationId", expression = "java(organizationRole.getOrganization() != null ? organizationRole.getOrganization().getId() : null)")
    @Named("toDto")
    OrganizationRoleDto toDto(Role organizationRole);

    @IterableMapping(qualifiedByName = "toDto")
    List<OrganizationRoleDto> toDtoList(List<Role> organizationRoles);

    @Named("permissionsToStringList")
    default List<String> permissionsToStringList(Set<Permission> permissions) {
        if (permissions == null) {
            return null;
        }
        return permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toList());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "roleType", constant = "ORGANIZATION")
    @Named("toEntity")
    Role toEntity(CreateOrganizationRoleDto role);
}