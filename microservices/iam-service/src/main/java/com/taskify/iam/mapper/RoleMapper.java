package com.taskify.iam.mapper;

import com.taskify.iam.dto.role.CreateRoleDto;
import com.taskify.iam.dto.role.RoleDto;
import com.taskify.iam.entity.Permission;
import com.taskify.iam.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "contextId", expression = "java(role.getContext() != null ? role.getContext().getId() : null)")
    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "permissionsToStringList")
    RoleDto toDto(Role role);

    List<RoleDto> toDtoList(List<Role> roles);

    @Named("permissionsToStringList")
    default List<String> permissionsToStringList(Set<Permission> permissions) {
        if (permissions == null) {
            return null;
        }
        return permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toList());
    }
}