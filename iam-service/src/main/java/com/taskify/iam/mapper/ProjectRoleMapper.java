package com.taskify.iam.mapper;

import com.taskify.iam.dto.role.CreateProjectRoleDto;
import com.taskify.iam.dto.role.ProjectRoleDto;
import com.taskify.iam.entity.Permission;
import com.taskify.iam.entity.ProjectRole;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProjectRoleMapper {
    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "permissionsToStringList")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "organizationId", source = "project.organizationId")
    @Named("toDto")
    ProjectRoleDto toDto(ProjectRole projectRole);

    @IterableMapping(qualifiedByName = "toDto")
    List<ProjectRoleDto> toDtoList(List<ProjectRole> projectRoles);

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
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Named("toEntity")
    ProjectRole toEntity(CreateProjectRoleDto role);
}