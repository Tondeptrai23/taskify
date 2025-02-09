package com.taskify.user.repository;

import com.taskify.user.entity.OrganizationRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrganizationRoleRepository extends JpaRepository<OrganizationRole, UUID> {
    @Query("SELECT r FROM OrganizationRole r WHERE r.isDefault = ?1")
    OrganizationRole getOrganizationRoleByDefault(boolean aDefault);
}
