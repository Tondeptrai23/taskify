package com.taskify.auth.presentation.controller;

import com.taskify.auth.application.dto.UserDto;
import com.taskify.auth.application.contracts.AuthApplicationService;
import com.taskify.auth.presentation.response.UserResponse;
import com.taskify.auth.presentation.mapper.AuthPresentationMapper;
import com.taskify.commoncore.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/internal")
public class InternalAuthController {
    private final AuthApplicationService authApplicationService;
    private final AuthPresentationMapper authPresentationMapper;

    public InternalAuthController(
            AuthApplicationService authApplicationService,
            AuthPresentationMapper authPresentationMapper
    ) {
        this.authApplicationService = authApplicationService;
        this.authPresentationMapper = authPresentationMapper;
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<UserResponse>> validate(@RequestHeader("Authorization") String token) {
        // Extract token from the Authorization header
        String tokenValue = token.startsWith("Bearer ") ? token.substring(7) : token;

        UserDto userDto = authApplicationService.verifyToken(tokenValue);
        UserResponse response = authPresentationMapper.toUserResponse(userDto);

        return ResponseEntity.ok(new ApiResponse<>(response));
    }
}