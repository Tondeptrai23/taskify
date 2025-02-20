package com.taskify.organization.repository;

import com.taskify.organization.entity.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocalUserRepository extends JpaRepository<LocalUser, UUID> {
    Optional<LocalUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
