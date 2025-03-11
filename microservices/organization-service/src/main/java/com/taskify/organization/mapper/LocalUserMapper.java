package com.taskify.organization.mapper;

import com.taskify.organization.dto.user.CreateLocalUserDto;
import com.taskify.organization.dto.user.LocalUserDto;
import com.taskify.organization.entity.LocalUser;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocalUserMapper {
    LocalUserDto toDto(LocalUser localUser);

    LocalUser toEntity(CreateLocalUserDto createLocalUserDto);

    List<LocalUserDto> toDtoList(List<LocalUser> localUsers);
}