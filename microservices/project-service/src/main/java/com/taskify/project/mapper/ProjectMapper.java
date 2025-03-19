package com.taskify.project.mapper;

import com.taskify.project.dto.project.CreateProjectDto;
import com.taskify.project.dto.project.ProjectDto;
import com.taskify.project.dto.project.UpdateProjectDto;
import com.taskify.project.entity.Project;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDto toDto(Project project);

    Project toEntity(CreateProjectDto createProjectDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Project project, UpdateProjectDto updateProjectDto);

    List<ProjectDto> toDtoList(List<Project> projects);
}