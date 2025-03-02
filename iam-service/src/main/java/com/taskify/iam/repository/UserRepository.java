package com.taskify.iam.repository;

import com.taskify.iam.entity.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends Neo4jRepository<User, UUID> {
}
