package com.taskify.iam.mapper;

import com.taskify.iam.dto.permission.PermissionDto;
import com.taskify.iam.dto.permission.PermissionGroupDto;
import com.taskify.iam.entity.Permission;
import com.taskify.iam.entity.PermissionGroup;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    @Named("toDto")
    PermissionDto toDto(Permission permission);

    @IterableMapping(qualifiedByName = "toDto")
    List<PermissionDto> toDtoList(List<Permission> permissions);

    @Named("toGroupDto")
    PermissionGroupDto toGroupDto(PermissionGroup group);

    @IterableMapping(qualifiedByName = "toGroupDto")
    List<PermissionGroupDto> toGroupDtoList(List<PermissionGroup> groups);

    default List<String> permissionsToStringList(Set<Permission> permissions) {
        if (permissions == null) {
            return null;
        }
        return permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toList());
    }
}