package com.taskify.auth.infrastructure.persistence.repository;

import com.taskify.auth.domain.entity.RefreshToken;
import com.taskify.auth.domain.repository.RefreshTokenRepository;
import com.taskify.auth.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import com.taskify.auth.infrastructure.persistence.mapper.RefreshTokenEntityMapper;
import com.taskify.auth.infrastructure.persistence.jpa.RefreshTokenJpaRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final RefreshTokenEntityMapper refreshTokenEntityMapper;

    public RefreshTokenRepositoryImpl(
            RefreshTokenJpaRepository refreshTokenJpaRepository,
            RefreshTokenEntityMapper refreshTokenEntityMapper
    ) {
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
        this.refreshTokenEntityMapper = refreshTokenEntityMapper;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity jpaEntity = refreshTokenEntityMapper.toJpaEntity(refreshToken);
        RefreshTokenJpaEntity savedEntity = refreshTokenJpaRepository.save(jpaEntity);
        return refreshTokenEntityMapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenJpaRepository.findByToken(token)
                .map(refreshTokenEntityMapper::toDomainEntity);
    }

    @Override
    @Transactional
    public void revokeToken(String token) {
        refreshTokenJpaRepository.revokeToken(token);
    }

    @Override
    @Transactional
    public void revokeAllTokensForUser(UUID userId) {
        refreshTokenJpaRepository.revokeAllTokensForUser(userId);
    }
}