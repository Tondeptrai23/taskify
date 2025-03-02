package com.taskify.iam.repository;

import com.taskify.iam.entity.LocalUser;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocalUserRepository extends Neo4jRepository<LocalUser, UUID> {
    @Query("MATCH (u:User) " +
            "WHERE u.id = $id " +
            "SET u.isDeleted = true")
    void delete(UUID id);

    @Query("MATCH (u:User) " +
            "WHERE u.id = $id " +
            "AND u.isDeleted = false " +
            "RETURN u")
    Optional<LocalUser> findById(UUID id);
}
