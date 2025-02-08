package com.taskify.user.mapper;

import com.taskify.user.dto.user.CreateUserDto;
import com.taskify.user.dto.user.UpdateUserDto;
import com.taskify.user.dto.user.UserBasicDto;
import com.taskify.user.dto.user.UserDto;
import com.taskify.user.entity.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role", source = "systemRole")
    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    User toEntity(UserBasicDto userBasicDto);

    @Mapping(target = "systemRole", source = "role")
    User toEntity(CreateUserDto userCreateDto);

    User toEntity(UpdateUserDto userUpdateDto);

    @Named("toBasicDto")
    @Mapping(target = "id", expression = "java(user.getId().toString())")
    UserBasicDto toBasicDto(User user);

    @IterableMapping(qualifiedByName = "toBasicDto")
    List<UserBasicDto> toBasicDtoList(List<User> users);
}