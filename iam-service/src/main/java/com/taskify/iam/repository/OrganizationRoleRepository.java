package com.taskify.iam.repository;

import com.taskify.iam.entity.Role;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRoleRepository extends Neo4jRepository<Role, UUID> {
    @Query("MATCH (o:Organization)<-[br:BELONGS_TO_ORG]-(r:Role) " +
            "WHERE o.id = $orgId AND r.roleType = 'ORGANIZATION' " +
            "OPTIONAL MATCH (r)-[h:HAS_PERMISSION]->(p:Permission) " +
            "RETURN r, o, br, collect(h), collect(p)")
    List<Role> findAllWithPermissionsInOrg(UUID orgId);

    @Query("MATCH (o:Organization)<-[br:BELONGS_TO_ORG]-(r:Role) " +
            "WHERE r.id = $roleId AND o.id = $orgId AND r.roleType = 'ORGANIZATION' " +
            "RETURN r, o, br")
    Optional<Role> findRoleByIdAndOrgId(UUID roleId, UUID orgId);

    @Query("MATCH (o:Organization)<-[br:BELONGS_TO_ORG]-(r:Role) " +
            "WHERE r.id = $roleId AND o.id = $orgId AND r.roleType = 'ORGANIZATION' " +
            "OPTIONAL MATCH (r)-[h:HAS_PERMISSION]->(p:Permission) " +
            "RETURN r, o, br, collect(h), collect(p)")
    Optional<Role> findRoleByIdAndOrgIdWithPermissions(UUID roleId, UUID orgId);

    @Query("MATCH (o:Organization)<-[br:BELONGS_TO_ORG]-(r:Role) " +
            "WHERE o.id = $orgId AND r.isDefault = true AND r.roleType = 'ORGANIZATION' " +
            "RETURN r, o, br")
    Optional<Role> findDefaultRoleByOrgId(UUID orgId);

    @Query("MATCH (r:Role) " +
            "WHERE r.id = $roleId " +
            "SET r.isDefault = $isDefault " +
            "RETURN r")
    Role updateDefaultRole(UUID roleId, boolean isDefault);
}