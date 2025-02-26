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
    @Query("MATCH (o:Organization)-[hr:HAS_ROLE]->(r:Role)-[h:HAS_PERMISSION]->(p:Permission) " +
            "WHERE o.id = $orgId " +
            "RETURN r, o, hr, collect(h), collect(p)")
    List<Role> findAllWithPermissionsInOrg(String orgId);

    @Query("MATCH (o:Organization)-[hr:HAS_ROLE]->(r:Role) " +
            "WHERE r.id = $roleId AND o.id = $orgId " +
            "RETURN r, o, hr")
    Optional<Role> findRoleByIdAndOrgId(UUID roleId, UUID orgId);

    @Query("MATCH (o:Organization)-[hr:HAS_ROLE]->(r:Role)-[h:HAS_PERMISSION]->(p:Permission) " +
            "WHERE r.id = $roleId AND o.id = $orgId " +
            "RETURN r, o, hr, collect(h), collect(p)")
    Optional<Role> findRoleByIdAndOrgIdWithPermissions(UUID roleId, UUID orgId);
}
