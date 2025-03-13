package com.taskify.iam.service.user;

import com.taskify.commoncore.error.resource.OrganizationNotFoundException;
import com.taskify.commoncore.error.resource.RoleNotFoundException;
import com.taskify.commoncore.error.resource.UserNotFoundException;
import com.taskify.iam.entity.LocalOrganization;
import com.taskify.iam.entity.LocalUser;
import com.taskify.iam.entity.Role;
import com.taskify.iam.entity.RoleType;
import com.taskify.iam.repository.LocalUserRepository;
import com.taskify.iam.repository.OrganizationRepository;
import com.taskify.iam.repository.OrganizationRoleRepository;
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
    private final OrganizationRepository organizationRepository;
    private final OrganizationRoleRepository roleRepository;
    private final Neo4jClient neo4jTemplate;

    @Autowired
    public UserRoleService(
            LocalUserRepository userRepository,
            OrganizationRepository organizationRepository,
            OrganizationRoleRepository roleRepository,
            Neo4jClient neo4jClient) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.roleRepository = roleRepository;
        this.neo4jTemplate = neo4jClient;
    }

    /**
     * Assigns an organization role to a user
     *
     * @param userId         The user ID
     * @param organizationId The organization ID
     * @param roleId         The role ID
     */
    @Transactional
    public void assignOrganizationRoleToUser(UUID userId, UUID organizationId, UUID roleId) {
        LocalUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        LocalOrganization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found with ID: " + organizationId));

        Role role = roleRepository.findRoleByIdAndOrgId(roleId, organizationId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with ID: " + roleId));

        if (role.getRoleType() != RoleType.ORGANIZATION) {
            throw new IllegalArgumentException("Role is not an organization role");
        }

        // Create relationship parameters
        Map<String, Object> params = new HashMap<>();
        params.put("organizationId", organizationId.toString());
        params.put("grantedAt", ZonedDateTime.now().toString());
        params.put("grantedBy", "SYSTEM");

        // First, remove any existing organization role relationship for this user and organization
        String removeExistingQuery = "MATCH (u:User {id: $userId})-[r:HAS_ORG_ROLE {organizationId: $organizationId}]->() " +
                "DELETE r";
        neo4jTemplate.query(removeExistingQuery)
                .bindAll(Map.of("userId", userId.toString(), "organizationId", organizationId.toString()))
                .run();

        // Create the new relationship
        String createRelationshipQuery = "MATCH (u:User {id: $userId}), (r:Role {id: $roleId}) " +
                "CREATE (u)-[rel:HAS_ORG_ROLE $params]->(r) " +
                "RETURN rel";

        neo4jTemplate.query(createRelationshipQuery)
                .bindAll(Map.of(
                        "userId", userId.toString(),
                        "roleId", roleId.toString(),
                        "params", params
                ))
                .run();

        log.info("Assigned role {} to user {} in organization {}", roleId, userId, organizationId);
    }

    /**
     * Removes an organization role from a user
     *
     * @param userId         The user ID
     * @param organizationId The organization ID
     */
    @Transactional
    public void removeOrganizationRoleFromUser(UUID userId, UUID organizationId) {
        // Verify the user and organization exist
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }

        if (!organizationRepository.existsById(organizationId)) {
            throw new OrganizationNotFoundException("Organization not found with ID: " + organizationId);
        }

        // Remove the relationship
        String query = "MATCH (u:User {id: $userId})-[r:HAS_ORG_ROLE {organizationId: $organizationId}]->() " +
                "DELETE r";

        neo4jTemplate.query(query)
                .bindAll(Map.of("userId", userId.toString(), "organizationId", organizationId.toString()))
                .run();

        log.info("Removed organization role from user {} in organization {}", userId, organizationId);
    }

    /**
     * Updates an organization role for a user
     *
     * @param userId         The user ID
     * @param organizationId The organization ID
     * @param roleId         The new role ID
     */
    @Transactional
    public void updateOrganizationRoleForUser(UUID userId, UUID organizationId, UUID roleId) {
        // This is effectively the same as assigning a new role
        assignOrganizationRoleToUser(userId, organizationId, roleId);
        log.info("Updated role to {} for user {} in organization {}", roleId, userId, organizationId);
    }
}