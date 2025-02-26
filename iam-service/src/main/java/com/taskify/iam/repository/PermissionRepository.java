package com.taskify.iam.repository;

import com.taskify.iam.entity.Permission;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PermissionRepository extends Neo4jRepository<Permission, Long> {
    List<Permission> findPermissionsByNameIn(Collection<String> names);
}
