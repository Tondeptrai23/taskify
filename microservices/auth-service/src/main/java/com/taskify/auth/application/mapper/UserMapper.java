package com.taskify.auth.application.mapper;

import com.taskify.auth.application.dto.UserDto;
import com.taskify.auth.application.dto.RegisterUserDto;
import com.taskify.auth.domain.entity.User;
import com.taskify.auth.domain.entity.SystemRole;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "systemRole", target = "role")
    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);

    @Mapping(target = "systemRole", constant = "USER")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterUserDto dto);

    @Named("mapSystemRole")
    default SystemRole mapSystemRole(String role) {
        return SystemRole.valueOf(role);
    }
}