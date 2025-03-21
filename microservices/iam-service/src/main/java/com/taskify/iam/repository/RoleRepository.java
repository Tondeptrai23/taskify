package com.taskify.iam.repository;

import com.taskify.iam.entity.Role;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends Neo4jRepository<Role, UUID> {
    @Query("MATCH (r:Role)-[rel:BELONGS_TO_CONTEXT]->(c:Context) " +
            "WHERE c.id = $contextId " +
            "OPTIONAL MATCH (r)-[h:HAS_PERMISSION]->(p:Permission) " +
            "RETURN r, c, rel, collect(h), collect(p)")
    List<Role> findAllWithPermissionsByContextId(UUID contextId);

    @Query("MATCH (r:Role)-[rel:BELONGS_TO_CONTEXT]->(c:Context) " +
            "WHERE r.id = $roleId AND c.id = $contextId " +
            "RETURN r, c, rel")
    Optional<Role> findRoleByIdAndContextId(UUID roleId, UUID contextId);

    @Query("MATCH (r:Role)-[rel:BELONGS_TO_CONTEXT]->(c:Context) " +
            "WHERE r.id = $roleId AND c.id = $contextId " +
            "OPTIONAL MATCH (r)-[h:HAS_PERMISSION]->(p:Permission) " +
            "RETURN r, c, rel, collect(h), collect(p)")
    Optional<Role> findRoleByIdAndContextIdWithPermissions(UUID roleId, UUID contextId);

    @Query("MATCH (r:Role)-[rel:BELONGS_TO_CONTEXT]->(c:Context) " +
            "WHERE c.id = $contextId AND r.isDefault = true " +
            "RETURN r, c, rel")
    Optional<Role> findDefaultRoleByContextId(UUID contextId);
}