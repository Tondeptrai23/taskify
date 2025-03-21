package com.taskify.iam.repository;

import com.taskify.iam.entity.Context;
import com.taskify.iam.entity.ContextType;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContextRepository extends Neo4jRepository<Context, UUID> {
    Optional<Context> findByExternalIdAndType(String externalId, ContextType type);

    @Query("MATCH (c:Context) WHERE c.type = $type RETURN c")
    List<Context> findAllByType(ContextType type);

    @Query("MATCH (c:Context {id: $contextId})-[:PARENT_OF*]->(child) RETURN child")
    List<Context> findAllDescendants(UUID contextId);

    @Query("MATCH (c:Context {id: $contextId})-[:CHILD_OF*]->(parent) RETURN parent")
    List<Context> findAllAncestors(UUID contextId);

    @Query("MATCH p = (c:Context {id: $contextId})-[:CHILD_OF*0..]->(parent) " +
            "WITH COLLECT(c) + COLLECT(parent) AS contexts " +
            "UNWIND contexts AS context " +
            "RETURN DISTINCT context")
    List<Context> findContextWithAncestors(UUID contextId);
}