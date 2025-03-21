package com.taskify.project.specification;

import com.taskify.project.dto.membership.MembershipCollectionRequest;
import com.taskify.project.entity.ProjectMembership;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;
import java.util.UUID;

public class ProjectMembershipSpecifications {
    public static Specification<ProjectMembership> withFilters(
            UUID projectId,
            MembershipCollectionRequest filter
    ) {
        return Specification.where(belongsToProject(projectId))
                .and(hasRole(filter.getRoleId()))
                .and(isActive(filter.getIsActive()))
                .and(hasJoinedDateBetween(filter.getJoinedFrom(), filter.getJoinedTo()));
    }

    private static Specification<ProjectMembership> belongsToProject(UUID projectId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("project").get("id"), projectId);
    }

    private static Specification<ProjectMembership> hasRole(UUID roleId) {
        return (root, query, criteriaBuilder) -> {
            if (roleId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("roleId"), roleId);
        };
    }

    private static Specification<ProjectMembership> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> {
            if (isActive == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("isActive"), isActive);
        };
    }

    private static Specification<ProjectMembership> hasJoinedDateBetween(
            ZonedDateTime joinedFrom,
            ZonedDateTime joinedTo
    ) {
        return (root, query, criteriaBuilder) -> {
            if (joinedFrom == null || joinedTo == null) {
                return null;
            }
            return criteriaBuilder.between(root.get("joinedAt"), joinedFrom, joinedTo);
        };
    }
}