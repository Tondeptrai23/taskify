package com.taskify.iam.repository;

import com.taskify.iam.entity.LocalOrganization;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrganizationRepository extends Neo4jRepository<LocalOrganization, UUID> {
}
