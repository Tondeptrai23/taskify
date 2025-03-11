package com.taskify.organization.specification;

import com.taskify.organization.dto.membership.MembershipCollectionRequest;
import com.taskify.organization.entity.Membership;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;
import java.util.UUID;

public class MembershipSpecifications {
    public static Specification<Membership> withFilters(
            UUID orgId,
            MembershipCollectionRequest filter
    ) {
        return Specification.where(belongsToOrganization(orgId))
                .and(hasRole(filter.getRoleId()))
                .and(isAdmin(filter.getIsAdmin()))
                .and(isActive(filter.getIsActive()))
                .and(hasJoinedDateBetween(filter.getJoinedFrom(), filter.getJoinedTo()));
    }

    private static Specification<Membership> belongsToOrganization(UUID orgId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("organization").get("id"), orgId);
    }

    private static Specification<Membership> hasRole(UUID roleId) {
        return (root, query, criteriaBuilder) -> {
            if (roleId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("role").get("id"), roleId);
        };
    }

    private static Specification<Membership> isAdmin(Boolean isAdmin) {
        return (root, query, criteriaBuilder) -> {
            if (isAdmin == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("isAdmin"), isAdmin);
        };
    }

    private static Specification<Membership> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> {
            if (isActive == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("isActive"), isActive);
        };
    }

    private static Specification<Membership> hasJoinedDateBetween(
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
