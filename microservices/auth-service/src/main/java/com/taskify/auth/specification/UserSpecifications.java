package com.taskify.auth.specification;

import com.taskify.auth.dto.user.UserCollectionRequest;
import com.taskify.auth.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;

public class UserSpecifications {
    public static Specification<User> withFilters(UserCollectionRequest filter) {
        return Specification.where(hasUsername(filter.getUsername()))
                .and(hasEmail(filter.getEmail()))
                .and(hasCreatedDateBetween(filter.getCreatedFrom(), filter.getCreatedTo()));
    }

    public static Specification<User> hasUsername(String username) {
        return (root, query, criteriaBuilder) ->
        {
            if (username == null) {
                return null;
            }

            return criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + username.toLowerCase() + "%");
        };
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) ->
        {
            if (email == null) {
                return null;
            }

            return criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    public static Specification<User> hasCreatedDateBetween(ZonedDateTime createdFrom, ZonedDateTime createdTo) {
        return (root, query, criteriaBuilder) ->
        {
            if (createdFrom == null || createdTo == null) {
                return null;
            }

            return criteriaBuilder.between(root.get("createdAt"), createdFrom, createdTo);
        };
    }
}
