package com.taskify.project.repository;

import com.taskify.project.entity.ProjectMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectMembershipRepository extends JpaRepository<ProjectMembership, UUID>,
        JpaSpecificationExecutor<ProjectMembership> {

    @Query("SELECT m FROM ProjectMembership m " +
            "JOIN FETCH m.user " +
            "WHERE m.project.id = :projectId AND m.user.id IN :userIds")
    List<ProjectMembership> findAllByProjectIdAndUserIdIn(UUID projectId, List<UUID> userIds);

    @Query("SELECT m FROM ProjectMembership m " +
            "JOIN FETCH m.user " +
            "WHERE m.project.id = :projectId AND m.user.id = :userId")
    Optional<ProjectMembership> findByProjectIdAndUserId(UUID projectId, UUID userId);

    List<ProjectMembership> findAllByProjectId(UUID projectId);

    List<ProjectMembership> findAllByUserId(UUID userId);
}