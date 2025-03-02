package com.taskify.iam.repository;

import com.taskify.iam.entity.LocalUser;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LocalUserRepository extends Neo4jRepository<LocalUser, UUID> {
}
