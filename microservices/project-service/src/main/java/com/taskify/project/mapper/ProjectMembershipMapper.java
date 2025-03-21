package com.taskify.project.mapper;

import com.taskify.project.dto.membership.ProjectMembershipDto;
import com.taskify.project.entity.LocalUser;
import com.taskify.project.entity.Project;
import com.taskify.project.entity.ProjectMembership;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ProjectMembershipMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    ProjectMembershipDto toDto(ProjectMembership membership);

    List<ProjectMembershipDto> toDtoList(List<ProjectMembership> memberships);

    default LocalUser userIdToUser(UUID userId) {
        if (userId == null) return null;
        LocalUser user = new LocalUser();
        user.setId(userId);
        return user;
    }

    default Project projectIdToProject(UUID projectId) {
        if (projectId == null) return null;
        Project project = new Project();
        project.setId(projectId);
        return project;
    }
}