package com.taskify.organization.specification;

import com.taskify.organization.dto.user.LocalUserCollectionRequest;
import com.taskify.organization.entity.LocalUser;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;

public class LocalUserSpecifications {
    public static Specification<LocalUser> withFilters(LocalUserCollectionRequest filter) {
        return Specification.where(hasUsername(filter.getUsername()))
                .and(hasEmail(filter.getEmail()))
                .and(hasCreatedDateBetween(filter.getCreatedFrom(), filter.getCreatedTo()));
    }

    public static Specification<LocalUser> hasUsername(String username) {
        return (root, query, criteriaBuilder) ->
        {
            if (username == null) {
                return null;
            }

            return criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + username.toLowerCase() + "%");
        };
    }

    public static Specification<LocalUser> hasEmail(String email) {
        return (root, query, criteriaBuilder) ->
        {
            if (email == null) {
                return null;
            }

            return criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    public static Specification<LocalUser> hasCreatedDateBetween(ZonedDateTime createdFrom, ZonedDateTime createdTo) {
        return (root, query, criteriaBuilder) ->
        {
            if (createdFrom == null || createdTo == null) {
                return null;
            }

            return criteriaBuilder.between(root.get("createdAt"), createdFrom, createdTo);
        };
    }
}
