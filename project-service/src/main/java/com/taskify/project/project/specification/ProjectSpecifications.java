package com.taskify.project.project.specification;

import com.taskify.project.project.dto.ProjectCollectionRequest;
import com.taskify.project.project.entity.Project;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;
import java.util.List;

public class ProjectSpecifications {
    public static Specification<Project> withFilters(ProjectCollectionRequest query){
        return Specification.where(hasName(query.getName()))
                .and(withStatuses(query.getStatuses()))
                .and(hasCreatedDateBetween(query.getCreatedFrom(), query.getCreatedTo()));
    }

    public static Specification<Project> hasName(String name) {
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

    public static Specification<Project> withStatuses(List<String> statuses) {
        return (root, query, criteriaBuilder) -> {
            if (statuses == null || statuses.isEmpty()) {
                return null;
            }
            return root.get("status").in(statuses);
        };
    }

    public static Specification<Project> hasCreatedDateBetween(
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
