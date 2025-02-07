package com.taskify.user.mapper;

import com.taskify.user.dto.CreateUserDto;
import com.taskify.user.dto.UpdateUserDto;
import com.taskify.user.dto.UserBasicDto;
import com.taskify.user.dto.UserDto;
import com.taskify.user.entity.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    User toEntity(UserBasicDto userBasicDto);

    User toEntity(CreateUserDto userCreateDto);

    User toEntity(UpdateUserDto userUpdateDto);

    @Named("toBasicDto")
    @Mapping(target = "id", expression = "java(user.getId().toString())")
    UserBasicDto toBasicDto(User user);

    @IterableMapping(qualifiedByName = "toBasicDto")
    List<UserBasicDto> toBasicDtoList(List<User> users);
}