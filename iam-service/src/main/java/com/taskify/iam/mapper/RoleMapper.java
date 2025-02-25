package com.taskify.iam.mapper;


import com.taskify.iam.dto.role.RoleDto;
import com.taskify.iam.entity.Permission;
import com.taskify.iam.entity.Role;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "permissionsToStringList")
    @Named("toDto")
    RoleDto toDto(Role role);

    @IterableMapping(qualifiedByName = "toDto")
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
