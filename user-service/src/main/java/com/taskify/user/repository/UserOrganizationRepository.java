package com.taskify.user.repository;

import com.taskify.user.entity.UserOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserOrganizationRepository extends JpaRepository<UserOrganization, UUID>,
        JpaSpecificationExecutor<UserOrganization> {
    @Query("SELECT uo FROM UserOrganization uo " +
            "JOIN FETCH uo.user " +
            "JOIN FETCH uo.role " +
            "WHERE uo.organization.id = :orgId AND uo.user.id IN :userIds")
    List<UserOrganization> findAllByOrgIdAndUserIdIn(UUID orgId, List<UUID> userIds);

    @Query("SELECT uo FROM UserOrganization uo " +
            "JOIN FETCH uo.user " +
            "JOIN FETCH uo.role " +
            "WHERE uo.organization.id = :orgId AND uo.user.id = :userId")
    UserOrganization findByOrgIdAndUserId(UUID orgId, UUID userId);
}
