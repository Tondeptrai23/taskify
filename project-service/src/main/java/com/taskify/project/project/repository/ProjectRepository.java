package com.taskify.project.project.repository;

import com.taskify.project.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    boolean findProjectByName(String name);

    boolean existsProjectByName(String name);
}
