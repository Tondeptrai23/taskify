package com.taskify.auth.presentation.controller;

import com.taskify.auth.application.dto.AuthResultDto;
import com.taskify.auth.application.dto.LoginDto;
import com.taskify.auth.application.dto.RegisterUserDto;
import com.taskify.auth.application.dto.UserDto;
import com.taskify.auth.application.contracts.AuthApplicationService;
import com.taskify.auth.presentation.request.LoginRequest;
import com.taskify.auth.presentation.request.RefreshTokenRequest;
import com.taskify.auth.presentation.request.RegisterRequest;
import com.taskify.auth.presentation.response.LoginResponse;
import com.taskify.auth.presentation.response.RegisterResponse;
import com.taskify.auth.presentation.mapper.AuthPresentationMapper;
import com.taskify.commoncore.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthApplicationService authApplicationService;
    private final AuthPresentationMapper authPresentationMapper;

    public AuthController(
            AuthApplicationService authApplicationService,
            AuthPresentationMapper authPresentationMapper
    ) {
        this.authApplicationService = authApplicationService;
        this.authPresentationMapper = authPresentationMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginDto loginDto = authPresentationMapper.toLoginDto(request);
        AuthResultDto authResult = authApplicationService.login(loginDto);
        LoginResponse response = authPresentationMapper.toLoginResponse(authResult);

        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody RegisterRequest request) {
        RegisterUserDto registerDto = authPresentationMapper.toRegisterUserDto(request);
        UserDto userDto = authApplicationService.register(registerDto);
        RegisterResponse response = authPresentationMapper.toRegisterResponse(userDto);

        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@RequestBody RefreshTokenRequest request) {
        AuthResultDto authResult = authApplicationService.refreshToken(request.getRefreshToken());
        LoginResponse response = authPresentationMapper.toLoginResponse(authResult);

        return ResponseEntity.ok(new ApiResponse<>(response));
    }
}