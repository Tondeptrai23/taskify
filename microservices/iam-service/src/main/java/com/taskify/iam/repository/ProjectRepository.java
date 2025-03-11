package com.taskify.iam.repository;

import com.taskify.iam.entity.Project;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends Neo4jRepository<Project, UUID> {
    @Query("MATCH (p:Project)-[:BELONGS_TO]->(o:Organization) " +
            "WHERE p.id = $projectId AND o.id = $orgId " +
            "RETURN p, o")
    Optional<Project> findByIdAndOrganizationId(UUID projectId, UUID orgId);

    @Query("MATCH (p:Project)-[:BELONGS_TO]->(o:Organization) " +
            "WHERE o.id = $orgId " +
            "RETURN p, o")
    Iterable<Project> findAllByOrganizationId(UUID orgId);
}