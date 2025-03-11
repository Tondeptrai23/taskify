package com.taskify.iam.repository;

import com.taskify.iam.entity.PermissionGroup;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionGroupRepository extends Neo4jRepository<PermissionGroup, Long> {
    @Query("MATCH (pg:PermissionGroup)-[c:CONTAINS]->(p:Permission) RETURN pg, collect(c), collect(p)")
    List<PermissionGroup> findAllWithPermissions();
}