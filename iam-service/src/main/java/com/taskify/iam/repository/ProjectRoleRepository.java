package com.taskify.iam.repository;

import com.taskify.iam.entity.ProjectRole;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRoleRepository extends Neo4jRepository<ProjectRole, UUID> {
    @Query("MATCH (p:Project)-[hr:HAS_ROLE]->(r:ProjectRole) " +
            "WHERE p.id = $projectId " +
            "OPTIONAL MATCH (r)-[h:HAS_PERMISSION]->(pm:Permission) " +
            "RETURN r, p, hr, collect(h), collect(pm)")
    List<ProjectRole> findAllWithPermissionsInProject(UUID projectId);

    @Query("MATCH (p:Project)-[hr:HAS_ROLE]->(r:ProjectRole) " +
            "WHERE r.id = $roleId AND p.id = $projectId " +
            "RETURN r, p, hr")
    Optional<ProjectRole> findRoleByIdAndProjectId(UUID roleId, UUID projectId);

    @Query("MATCH (p:Project)-[hr:HAS_ROLE]->(r:ProjectRole) " +
            "WHERE r.id = $roleId AND p.id = $projectId " +
            "OPTIONAL MATCH (r)-[h:HAS_PERMISSION]->(pm:Permission) " +
            "RETURN r, p, hr, collect(h), collect(pm)")
    Optional<ProjectRole> findRoleByIdAndProjectIdWithPermissions(UUID roleId, UUID projectId);

    @Query("MATCH (p:Project)-[hr:HAS_ROLE]->(r:ProjectRole) " +
            "WHERE p.id = $projectId AND r.isDefault = true " +
            "RETURN r, p, hr")
    Optional<ProjectRole> findDefaultRoleByProjectId(UUID projectId);

    @Query("MATCH (p:Project)-[hr:HAS_ROLE]->(r:ProjectRole) " +
            "WHERE r.id = $roleId " +
            "SET r.isDefault = $isDefault " +
            "RETURN r, p, hr")
    ProjectRole updateDefaultRole(UUID roleId, boolean isDefault);
}