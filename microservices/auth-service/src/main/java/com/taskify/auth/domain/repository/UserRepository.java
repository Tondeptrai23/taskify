package com.taskify.auth.domain.repository;

import com.taskify.auth.domain.entity.User;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    List<User> findAllById(Iterable<UUID> ids);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    void deleteById(UUID id);
}