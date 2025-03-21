package com.taskify.iam.service.role;

import com.taskify.commoncore.error.resource.RoleNotFoundException;
import com.taskify.commoncore.error.resource.UserNotFoundException;
import com.taskify.iam.entity.Context;
import com.taskify.iam.entity.LocalUser;
import com.taskify.iam.entity.Role;
import com.taskify.iam.exception.ContextNotFoundException;
import com.taskify.iam.repository.ContextRepository;
import com.taskify.iam.repository.LocalUserRepository;
import com.taskify.iam.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class UserRoleService {

    private final LocalUserRepository userRepository;
    private final ContextRepository contextRepository;
    private final RoleRepository roleRepository;
    private final Neo4jClient neo4jTemplate;

    @Autowired
    public UserRoleService(
            LocalUserRepository userRepository,
            ContextRepository contextRepository,
            RoleRepository roleRepository,
            Neo4jClient neo4jClient) {
        this.userRepository = userRepository;
        this.contextRepository = contextRepository;
        this.roleRepository = roleRepository;
        this.neo4jTemplate = neo4jClient;
    }

    @Transactional
    public void assignRoleToUser(UUID userId, UUID contextId, UUID roleId) {
        LocalUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Context context = contextRepository.findById(contextId)
                .orElseThrow(() -> new ContextNotFoundException("Context not found with ID: " + contextId));

        Role role = roleRepository.findRoleByIdAndContextId(roleId, contextId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with ID: " + roleId));

        // Create relationship parameters
        Map<String, Object> params = new HashMap<>();
        params.put("contextId", contextId.toString());
        params.put("grantedAt", ZonedDateTime.now().toString());
        params.put("grantedBy", "SYSTEM");

        // First, remove any existing role relationship for this user and context
        String removeExistingQuery = "MATCH (u:User {id: $userId})-[r:HAS_ROLE {contextId: $contextId}]->() " +
                "DELETE r";
        neo4jTemplate.query(removeExistingQuery)
                .bindAll(Map.of("userId", userId.toString(), "contextId", contextId.toString()))
                .run();

        // Create the new relationship
        String createRelationshipQuery = "MATCH (u:User {id: $userId}), (r:Role {id: $roleId}) " +
                "CREATE (u)-[rel:HAS_ROLE $params]->(r) " +
                "RETURN rel";

        neo4jTemplate.query(createRelationshipQuery)
                .bindAll(Map.of(
                        "userId", userId.toString(),
                        "roleId", roleId.toString(),
                        "params", params
                ))
                .run();

        log.info("Assigned role {} to user {} in context {}", roleId, userId, contextId);
    }

    @Transactional
    public void removeRoleFromUser(UUID userId, UUID contextId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }

        if (!contextRepository.existsById(contextId)) {
            throw new ContextNotFoundException("Context not found with ID: " + contextId);
        }

        // Remove the relationship
        String query = "MATCH (u:User {id: $userId})-[r:HAS_ROLE {contextId: $contextId}]->() " +
                "DELETE r";

        neo4jTemplate.query(query)
                .bindAll(Map.of("userId", userId.toString(), "contextId", contextId.toString()))
                .run();

        log.info("Removed role from user {} in context {}", userId, contextId);
    }

    @Transactional
    public void updateUserRole(UUID userId, UUID contextId, UUID roleId) {
        // This is effectively the same as assigning a new role
        assignRoleToUser(userId, contextId, roleId);
        log.info("Updated role to {} for user {} in context {}", roleId, userId, contextId);
    }
}