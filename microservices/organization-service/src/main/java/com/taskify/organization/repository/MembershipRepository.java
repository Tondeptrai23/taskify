package com.taskify.organization.repository;

import com.taskify.organization.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID>,
        JpaSpecificationExecutor<Membership> {

    @Query("SELECT m FROM Membership m " +
            "JOIN FETCH m.user " +
            "WHERE m.organization.id = :orgId AND m.user.id IN :userIds")
    List<Membership> findAllByOrgIdAndUserIdIn(UUID orgId, List<UUID> userIds);

    @Query("SELECT m FROM Membership m " +
            "JOIN FETCH m.user " +
            "WHERE m.organization.id = :orgId AND m.user.id = :userId")
    Optional<Membership> findByOrgIdAndUserId(UUID orgId, UUID userId);

    List<Membership> findAllByOrganizationId(UUID organizationId);

    List<Membership> findAllByUserId(UUID userId);
}