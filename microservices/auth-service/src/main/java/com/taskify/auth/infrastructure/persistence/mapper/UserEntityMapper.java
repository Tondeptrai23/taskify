package com.taskify.auth.infrastructure.persistence.mapper;

import com.taskify.auth.domain.entity.User;
import com.taskify.auth.infrastructure.persistence.entity.UserJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {
    UserJpaEntity toJpaEntity(User user);

    User toDomainEntity(UserJpaEntity userJpaEntity);
}