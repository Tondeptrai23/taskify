package com.taskify.auth.old.controller;

import com.taskify.auth.dto.auth.*;
import com.taskify.auth.old.dto.auth.*;
import com.taskify.auth.old.service.AuthService;
import com.taskify.commoncore.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService _authService;

    @Autowired
    public AuthController(AuthService authService) {
        this._authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest loginRequest
    ){
        var response = _authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(new ApiResponse<>(new LoginResponse(response)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody RegisterRequest request) {
        var response = _authService.registerUser(request);
        return ResponseEntity.ok(new ApiResponse<>(new RegisterResponse(response)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthTokens>> refresh(
            @RequestBody RefreshRequest req
    ){
        var response = _authService.refresh(req.getRefreshToken());
        return ResponseEntity.ok(new ApiResponse<>(response));
    }
}