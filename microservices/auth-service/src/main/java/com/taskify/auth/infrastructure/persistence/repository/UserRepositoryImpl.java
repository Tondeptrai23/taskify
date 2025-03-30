package com.taskify.auth.infrastructure.persistence.repository;

import com.taskify.auth.domain.entity.User;
import com.taskify.auth.domain.repository.UserRepository;
import com.taskify.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.taskify.auth.infrastructure.persistence.mapper.UserEntityMapper;
import com.taskify.auth.infrastructure.persistence.jpa.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final UserEntityMapper userEntityMapper;

    public UserRepositoryImpl(UserJpaRepository userJpaRepository, UserEntityMapper userEntityMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userEntityMapper = userEntityMapper;
    }

    @Override
    public User save(User user) {
        UserJpaEntity jpaEntity = userEntityMapper.toJpaEntity(user);
        UserJpaEntity savedEntity = userJpaRepository.save(jpaEntity);
        return userEntityMapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id)
                .map(userEntityMapper::toDomainEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(userEntityMapper::toDomainEntity);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(userEntityMapper::toDomainEntity);
    }

    @Override
    public List<User> findAllById(Iterable<UUID> ids) {
        return userJpaRepository.findAllById(ids).stream()
                .map(userEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public void deleteById(UUID id) {
        userJpaRepository.deleteById(id);
    }
}