package com.taskify.user.specification;

import com.taskify.user.dto.organization.OrganizationMemberCollectionRequest;
import com.taskify.user.entity.UserOrganization;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;
import java.util.UUID;

public class UserOrganizationSpecifications {
    public static Specification<UserOrganization> withFilters(
            UUID orgId,
            OrganizationMemberCollectionRequest filter
    ) {
        return Specification.where(belongsToOrganization(orgId))
                .and(hasRole(filter.getRoleId()))
                .and(isAdmin(filter.getIsAdmin()))
                .and(isActive(filter.getIsActive()))
                .and(hasJoinedDateBetween(filter.getJoinedFrom(), filter.getJoinedTo()));
    }

    private static Specification<UserOrganization> belongsToOrganization(UUID orgId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("orgId"), orgId);
    }

    private static Specification<UserOrganization> hasRole(String roleId) {
        return (root, query, criteriaBuilder) -> {
            if (roleId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("orgRoleId"), UUID.fromString(roleId));
        };
    }

    private static Specification<UserOrganization> isAdmin(Boolean isAdmin) {
        return (root, query, criteriaBuilder) -> {
            if (isAdmin == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("isAdmin"), isAdmin);
        };
    }

    private static Specification<UserOrganization> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> {
            if (isActive == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("isActive"), isActive);
        };
    }

    private static Specification<UserOrganization> hasJoinedDateBetween(
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