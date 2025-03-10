package com.taskify.iam.repository;

import com.taskify.iam.entity.Role;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRoleRepository extends Neo4jRepository<Role, UUID> {
    @Query("MATCH (p:Project)<-[br:BELONGS_TO_PROJECT]-(r:Role) " +
            "WHERE p.id = $projectId AND r.roleType = 'PROJECT' " +
            "OPTIONAL MATCH (r)-[h:HAS_PERMISSION]->(pm:Permission) " +
            "RETURN r, p, br, collect(h), collect(pm)")
    List<Role> findAllWithPermissionsInProject(UUID projectId);

    @Query("MATCH (p:Project)<-[br:BELONGS_TO_PROJECT]-(r:Role) " +
            "WHERE r.id = $roleId AND p.id = $projectId AND r.roleType = 'PROJECT' " +
            "RETURN r, p, br")
    Optional<Role> findRoleByIdAndProjectId(UUID roleId, UUID projectId);

    @Query("MATCH (p:Project)<-[br:BELONGS_TO_PROJECT]-(r:Role) " +
            "WHERE r.id = $roleId AND p.id = $projectId AND r.roleType = 'PROJECT' " +
            "OPTIONAL MATCH (r)-[h:HAS_PERMISSION]->(pm:Permission) " +
            "RETURN r, p, br, collect(h), collect(pm)")
    Optional<Role> findRoleByIdAndProjectIdWithPermissions(UUID roleId, UUID projectId);

    @Query("MATCH (p:Project)<-[br:BELONGS_TO_PROJECT]-(r:Role) " +
            "WHERE p.id = $projectId AND r.isDefault = true AND r.roleType = 'PROJECT' " +
            "RETURN r, p, br")
    Optional<Role> findDefaultRoleByProjectId(UUID projectId);

    @Query("MATCH (r:Role) " +
            "WHERE r.id = $roleId " +
            "SET r.isDefault = $isDefault " +
            "RETURN r")
    Role updateDefaultRole(UUID roleId, boolean isDefault);
}