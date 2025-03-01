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
    @Query("MATCH (o:Organization)-[hr:HAS_ROLE]->(r:OrganizationRole) " +
            "WHERE o.id = $orgId " +
            "OPTIONAL MATCH (r)-[h:HAS_PERMISSION]->(p:Permission) " +
            "RETURN r, o, hr, collect(h), collect(p)")
    List<OrganizationRole> findAllWithPermissionsInOrg(UUID orgId);

    @Query("MATCH (o:Organization)-[hr:HAS_ROLE]->(r:OrganizationRole) " +
            "WHERE r.id = $roleId AND o.id = $orgId " +
            "RETURN r, o, hr")
    Optional<OrganizationRole> findRoleByIdAndOrgId(UUID roleId, UUID orgId);

    @Query("MATCH (o:Organization)-[hr:HAS_ROLE]->(r:OrganizationRole) " +
            "WHERE r.id = $roleId AND o.id = $orgId " +
            "OPTIONAL MATCH (r)-[h:HAS_PERMISSION]->(p:Permission) " +
            "RETURN r, o, hr, collect(h), collect(p)")
    Optional<OrganizationRole> findRoleByIdAndOrgIdWithPermissions(UUID roleId, UUID orgId);

    @Query("MATCH (o:Organization)-[hr:HAS_ROLE]->(r:OrganizationRole) " +
            "WHERE o.id = $orgId AND r.isDefault = true " +
            "RETURN r, o, hr")
    Optional<OrganizationRole> findDefaultRoleByOrgId(UUID orgId);

    @Query("MATCH (o:Organization)-[hr:HAS_ROLE]->(r:OrganizationRole) " +
            "WHERE r.id = $roleId AND r.isDefault = $isDefault " +
            "RETURN r, o, hr")
    OrganizationRole updateDefaultRole(UUID roleId, boolean isDefault);
}
