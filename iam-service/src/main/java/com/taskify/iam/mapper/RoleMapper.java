package com.taskify.iam.mapper;


import com.taskify.iam.dto.role.CreateRoleDto;
import com.taskify.iam.dto.role.RoleDto;
import com.taskify.iam.entity.Permission;
import com.taskify.iam.entity.OrganizationRole;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "permissionsToStringList")
    @Mapping(target = "organizationId", source = "organization.id")
    @Named("toDto")
    RoleDto toDto(OrganizationRole organizationRole);

    @IterableMapping(qualifiedByName = "toDto")
    List<RoleDto> toDtoList(List<OrganizationRole> organizationRoles);

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
    @Mapping(target = "permissions", source = "permissions", ignore = true)
    @Named("toEntity")
    OrganizationRole toEntity(CreateRoleDto role);
}
