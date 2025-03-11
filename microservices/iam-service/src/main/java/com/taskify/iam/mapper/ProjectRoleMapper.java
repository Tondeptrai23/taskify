package com.taskify.iam.mapper;

import com.taskify.iam.dto.role.CreateProjectRoleDto;
import com.taskify.iam.dto.role.ProjectRoleDto;
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
public interface ProjectRoleMapper {
    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "permissionsToStringList")
    @Mapping(target = "projectId", expression = "java(projectRole.getProject() != null ? projectRole.getProject().getId() : null)")
    @Named("toDto")
    ProjectRoleDto toDto(Role projectRole);

    @IterableMapping(qualifiedByName = "toDto")
    List<ProjectRoleDto> toDtoList(List<Role> projectRoles);

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
    @Mapping(target = "roleType", constant = "PROJECT")
    @Named("toEntity")
    Role toEntity(CreateProjectRoleDto role);
}