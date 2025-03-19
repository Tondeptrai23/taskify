package com.taskify.project.repository;

import com.taskify.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID>, JpaSpecificationExecutor<Project> {

    List<Project> findAllByOrganizationId(UUID organizationId);

    @Query("SELECT p FROM Project p WHERE p.organizationId = :organizationId AND p.id = :projectId")
    Optional<Project> findByIdAndOrganizationId(UUID projectId, UUID organizationId);

    boolean existsByOrganizationIdAndKey(UUID organizationId, String key);

    Optional<Project> findByOrganizationIdAndKey(UUID organizationId, String key);
}