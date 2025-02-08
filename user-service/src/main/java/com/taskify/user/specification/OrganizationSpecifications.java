package com.taskify.user.specification;

import com.taskify.user.dto.organization.OrganizationCollectionRequest;
import com.taskify.user.entity.Organization;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;

public class OrganizationSpecifications {
    public static Specification<Organization> withFilters(OrganizationCollectionRequest filter) {
        return Specification.where(hasName(filter.getName()))
                .and(hasCreatedDateBetween(filter.getCreatedFrom(), filter.getCreatedTo()));
    }

    private static Specification<Organization> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null) {
                return null;
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%"
            );
        };
    }

    private static Specification<Organization> hasCreatedDateBetween(
            ZonedDateTime createdFrom,
            ZonedDateTime createdTo
    ) {
        return (root, query, criteriaBuilder) -> {
            if (createdFrom == null || createdTo == null) {
                return null;
            }
            return criteriaBuilder.between(
                    root.get("createdAt"),
                    createdFrom,
                    createdTo
            );
        };
    }
}