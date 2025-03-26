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
import org.springframework.stereotype.Component;

@Component
public class AuthPresentationMapper {

    public LoginDto toLoginDto(LoginRequest request) {
        LoginDto dto = new LoginDto();
        dto.setUsername(request.getUsername());
        dto.setPassword(request.getPassword());
        return dto;
    }

    public LoginResponse toLoginResponse(AuthResultDto authResult) {
        LoginResponse response = new LoginResponse();
        response.setId(authResult.getUserId());
        response.setUsername(authResult.getUsername());
        response.setEmail(authResult.getEmail());
        response.setRole(authResult.getRole());
        response.setAccessToken(authResult.getAccessToken());
        response.setRefreshToken(authResult.getRefreshToken());
        return response;
    }

    public RegisterUserDto toRegisterUserDto(RegisterRequest request) {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setUsername(request.getUsername());
        dto.setEmail(request.getEmail());
        dto.setPassword(request.getPassword());
        return dto;
    }

    public RegisterResponse toRegisterResponse(UserDto userDto) {
        RegisterResponse response = new RegisterResponse();
        response.setId(userDto.getId());
        response.setUsername(userDto.getUsername());
        response.setEmail(userDto.getEmail());
        return response;
    }

    public UserResponse toUserResponse(UserDto userDto) {
        UserResponse response = new UserResponse();
        response.setId(userDto.getId());
        response.setUsername(userDto.getUsername());
        response.setEmail(userDto.getEmail());
        response.setRole(userDto.getRole());
        response.setCreatedAt(userDto.getCreatedAt());
        response.setUpdatedAt(userDto.getUpdatedAt());
        return response;
    }
}