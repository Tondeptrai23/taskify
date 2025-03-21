package com.taskify.iam.service.user;

import com.taskify.commoncore.error.resource.ProjectNotFoundException;
import com.taskify.commoncore.error.resource.RoleNotFoundException;
import com.taskify.commoncore.error.resource.UserNotFoundException;
import com.taskify.iam.entity.LocalOrganization;
import com.taskify.iam.entity.LocalUser;
import com.taskify.iam.entity.Project;
import com.taskify.iam.entity.Role;
import com.taskify.iam.entity.RoleType;
import com.taskify.iam.repository.LocalUserRepository;
import com.taskify.iam.repository.OrganizationRepository;
import com.taskify.iam.repository.ProjectRepository;
import com.taskify.iam.repository.ProjectRoleRepository;
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
public class UserProjectRoleService {

    private final LocalUserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final ProjectRoleRepository roleRepository;
    private final Neo4jClient neo4jTemplate;

    @Autowired
    public UserProjectRoleService(
            LocalUserRepository userRepository,
            ProjectRepository projectRepository,
            OrganizationRepository organizationRepository,
            ProjectRoleRepository roleRepository,
            Neo4jClient neo4jClient) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
        this.roleRepository = roleRepository;
        this.neo4jTemplate = neo4jClient;
    }

    /**
     * Assigns a project role to a user
     *
     * @param userId         The user ID
     * @param projectId      The project ID
     * @param organizationId The organization ID (for context)
     * @param roleId         The role ID
     */
    @Transactional
    public void assignProjectRoleToUser(UUID userId, UUID projectId, UUID organizationId, UUID roleId) {
        LocalUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " + projectId));

        // Verify organization exists (needed for relationship property)
        LocalOrganization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ProjectNotFoundException("Organization not found with ID: " + organizationId));

        Role role = roleRepository.findRoleByIdAndProjectId(roleId, projectId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with ID: " + roleId));

        if (role.getRoleType() != RoleType.PROJECT) {
            throw new IllegalArgumentException("Role is not a project role");
        }

        // Create relationship parameters
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId.toString());
        params.put("organizationId", organizationId.toString());
        params.put("grantedAt", ZonedDateTime.now().toString());
        params.put("grantedBy", "SYSTEM");

        // First, remove any existing project role relationship for this user and project
        String removeExistingQuery = "MATCH (u:User {id: $userId})-[r:HAS_PROJECT_ROLE {projectId: $projectId}]->() " +
                "DELETE r";
        neo4jTemplate.query(removeExistingQuery)
                .bindAll(Map.of("userId", userId.toString(), "projectId", projectId.toString()))
                .run();

        // Create the new relationship
        String createRelationshipQuery = "MATCH (u:User {id: $userId}), (r:Role {id: $roleId}) " +
                "CREATE (u)-[rel:HAS_PROJECT_ROLE $params]->(r) " +
                "RETURN rel";

        neo4jTemplate.query(createRelationshipQuery)
                .bindAll(Map.of(
                        "userId", userId.toString(),
                        "roleId", roleId.toString(),
                        "params", params
                ))
                .run();

        log.info("Assigned project role {} to user {} in project {}", roleId, userId, projectId);
    }

    /**
     * Removes a project role from a user
     *
     * @param userId         The user ID
     * @param projectId      The project ID
     * @param organizationId The organization ID (not functionally needed but for logging context)
     */
    @Transactional
    public void removeProjectRoleFromUser(UUID userId, UUID projectId, UUID organizationId) {
        // Verify the user and project exist
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }

        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException("Project not found with ID: " + projectId);
        }

        // Remove the relationship
        String query = "MATCH (u:User {id: $userId})-[r:HAS_PROJECT_ROLE {projectId: $projectId}]->() " +
                "DELETE r";

        neo4jTemplate.query(query)
                .bindAll(Map.of("userId", userId.toString(), "projectId", projectId.toString()))
                .run();

        log.info("Removed project role from user {} in project {} for organization {}", userId, projectId, organizationId);
    }

    /**
     * Updates a project role for a user
     *
     * @param userId         The user ID
     * @param projectId      The project ID
     * @param organizationId The organization ID (for context)
     * @param roleId         The new role ID
     */
    @Transactional
    public void updateProjectRoleForUser(UUID userId, UUID projectId, UUID organizationId, UUID roleId) {
        // This is effectively the same as assigning a new role
        assignProjectRoleToUser(userId, projectId, organizationId, roleId);
        log.info("Updated project role to {} for user {} in project {}", roleId, userId, projectId);
    }
}