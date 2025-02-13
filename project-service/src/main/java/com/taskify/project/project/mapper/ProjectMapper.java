package com.taskify.project.project.mapper;

import com.taskify.project.project.dto.ProjectDto;
import com.taskify.project.project.entity.Project;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDto toDto(Project project);

    Project toEntity(ProjectDto projectDto);

    @IterableMapping(qualifiedByName = "toDto")
    List<ProjectDto> toDtoList(List<Project> projects);
}
