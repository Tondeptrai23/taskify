package com.taskify.auth.infrastructure.persistence.mapper;

import com.taskify.auth.domain.entity.RefreshToken;
import com.taskify.auth.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefreshTokenEntityMapper {
    RefreshTokenJpaEntity toJpaEntity(RefreshToken domainEntity);

    RefreshToken toDomainEntity(RefreshTokenJpaEntity jpaEntity);
}
