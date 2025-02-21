package com.taskify.auth.controller;

import com.taskify.auth.dto.auth.LoginRequest;
import com.taskify.auth.dto.auth.LoginResponse;
import com.taskify.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService _authService;

    @Autowired
    public AuthController(AuthService authService) {
        this._authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest
    ){
        var response = _authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(new LoginResponse(response));
    }
}
