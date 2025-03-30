package com.taskify.auth.presentation.mapper;

import com.taskify.auth.application.dto.AuthResultDto;
import com.taskify.auth.application.dto.LoginDto;
import com.taskify.auth.application.dto.RegisterUserDto;
import com.taskify.auth.application.dto.UserDto;
import com.taskify.auth.presentation.request.LoginRequest;
import com.taskify.auth.presentation.request.RegisterRequest;
import com.taskify.auth.presentation.response.LoginResponse;
import com.taskify.auth.presentation.response.RegisterResponse;
import com.taskify.auth.presentation.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthPresentationMapper {

    LoginDto toLoginDto(LoginRequest request);

    @Mapping(target = "id", source = "authResult.userId")
    LoginResponse toLoginResponse(AuthResultDto authResult);

    RegisterUserDto toRegisterUserDto(RegisterRequest request);

    RegisterResponse toRegisterResponse(UserDto userDto);

    UserResponse toUserResponse(UserDto userDto);
}