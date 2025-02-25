package com.taskify.iam.repository;

import com.taskify.iam.entity.Role;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoleRepository extends Neo4jRepository<Role, UUID> {
    @Query("MATCH (r:Role)-[h:HAS_PERMISSION]->(p:Permission) " +
            "RETURN r,h,p")
    List<Role> findAllWithPermissions();
}
