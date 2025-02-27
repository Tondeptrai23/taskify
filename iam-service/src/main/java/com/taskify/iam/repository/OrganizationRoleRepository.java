package com.taskify.iam.repository;

import com.taskify.iam.entity.OrganizationRole;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRoleRepository extends Neo4jRepository<OrganizationRole, UUID> {
    @Query("MATCH (o:Organization)-[hr:HAS_ROLE]->(r:OrganizationRole)-[h:HAS_PERMISSION]->(p:Permission) " +
            "WHERE o.id = $orgId " +
            "RETURN r, o, hr, collect(h), collect(p)")
    List<OrganizationRole> findAllWithPermissionsInOrg(String orgId);

    @Query("MATCH (o:Organization)-[hr:HAS_ROLE]->(r:OrganizationRole) " +
            "WHERE r.id = $roleId AND o.id = $orgId " +
            "RETURN r, o, hr")
    Optional<OrganizationRole> findRoleByIdAndOrgId(UUID roleId, UUID orgId);

    @Query("MATCH (o:Organization)-[hr:HAS_ROLE]->(r:OrganizationRole)-[h:HAS_PERMISSION]->(p:Permission) " +
            "WHERE r.id = $roleId AND o.id = $orgId " +
            "RETURN r, o, hr, collect(h), collect(p)")
    Optional<OrganizationRole> findRoleByIdAndOrgIdWithPermissions(UUID roleId, UUID orgId);
}
