package com.taskify.project.specification;

import com.taskify.project.dto.project.ProjectCollectionRequest;
import com.taskify.project.entity.Project;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;
import java.util.UUID;

public class ProjectSpecifications {

    public static Specification<Project> withFilters(UUID organizationId, ProjectCollectionRequest filter) {
        return Specification.where(belongsToOrganization(organizationId))
                .and(hasName(filter.getName()))
                .and(hasKey(filter.getKey()))
                .and(hasCreatedDateBetween(filter.getCreatedFrom(), filter.getCreatedTo()));
    }

    private static Specification<Project> belongsToOrganization(UUID organizationId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("organizationId"), organizationId);
    }

    private static Specification<Project> hasName(String name) {
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

    private static Specification<Project> hasKey(String key) {
        return (root, query, criteriaBuilder) -> {
            if (key == null) {
                return null;
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("key")),
                    "%" + key.toLowerCase() + "%"
            );
        };
    }

    private static Specification<Project> hasCreatedDateBetween(
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