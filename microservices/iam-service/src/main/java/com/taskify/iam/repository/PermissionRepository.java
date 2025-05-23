package com.taskify.iam.repository;

import com.taskify.iam.entity.Permission;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface PermissionRepository extends Neo4jRepository<Permission, Long> {
    List<Permission> findPermissionsByNameIn(Collection<String> names);

    @Query("MATCH (p:Permission) RETURN p")
    List<Permission> findAllPermissions();

    @Query("MATCH (pg:PermissionGroup)-[:CONTAINS]->(p:Permission) " +
            "WHERE pg.id = $groupId RETURN p")
    List<Permission> findPermissionsByGroupId(Long groupId);

    @Query("MATCH (u:User)-[r:HAS_ORG_ROLE {organizationId: $organizationId}]->(role:Role)-[hp:HAS_PERMISSION]->(p:Permission) " +
            "WHERE u.id = $userId AND u.isDeleted = false AND role.roleType = 'ORGANIZATION' " +
            "RETURN DISTINCT p")
    List<Permission> findOrganizationPermissionsOfUser(UUID organizationId, UUID userId);

    @Query("MATCH (u:User)-[r:HAS_PROJECT_ROLE {projectId: $projectId}]->(role:Role)-[hp:HAS_PERMISSION]->(p1:Permission) " +
            "WHERE u.id = $userId AND u.isDeleted = false AND role.roleType = 'PROJECT' " +
            "WITH u, COLLECT(DISTINCT p1) AS projectPermissions " +
            "MATCH (u)-[:HAS_ORG_ROLE {organizationId: $organizationId}]->(orgRole:Role)-[:HAS_PERMISSION]->(p2:Permission) " +
            "WHERE orgRole.roleType = 'ORGANIZATION' " +
            "WITH u, projectPermissions, COLLECT(DISTINCT p2) AS orgPermissions " +
            "RETURN projectPermissions + orgPermissions AS allPermissions")
    List<Permission> findPermissionsOfUser(UUID userId, UUID projectId, UUID organizationId);

    @Query("MATCH (u:User)-[r:HAS_PROJECT_ROLE {projectId: $projectId}]->(role:Role)-[hp:HAS_PERMISSION]->(p:Permission) " +
            "WHERE u.id = $userId AND u.isDeleted = false AND role.roleType = 'PROJECT' " +
            "RETURN DISTINCT p")
    List<Permission> findProjectPermissionsOfUser(UUID projectId, UUID userId);

    @Query("MATCH (u:User {id: $userId})-[r:HAS_ROLE]->(role:Role)-[hp:HAS_PERMISSION]->(p:Permission) " +
            "WHERE r.contextId IN $contextIds " +
            "RETURN DISTINCT p")
    List<Permission> findUserPermissionsInContexts(UUID userId, List<UUID> contextIds);
}