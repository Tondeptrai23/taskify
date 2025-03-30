package com.taskify.auth.presentation.controller;

import com.taskify.auth.application.dto.UserDto;
import com.taskify.auth.application.contracts.UserApplicationService;
import com.taskify.auth.presentation.request.UpdateUserRequest;
import com.taskify.auth.presentation.response.UserResponse;
import com.taskify.auth.presentation.mapper.AuthPresentationMapper;
import com.taskify.commoncore.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserApplicationService userApplicationService;
    private final AuthPresentationMapper authPresentationMapper;

    public UserController(
            UserApplicationService userApplicationService,
            AuthPresentationMapper authPresentationMapper
    ) {
        this.userApplicationService = userApplicationService;
        this.authPresentationMapper = authPresentationMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UserDto userDto = userApplicationService.getUserById(id);
        UserResponse response = authPresentationMapper.toUserResponse(userDto);

        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @GetMapping("/batch")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByIds(@RequestParam List<UUID> ids) {
        List<UserDto> userDtos = userApplicationService.getUsersByIds(ids);
        List<UserResponse> responses = userDtos.stream()
                .map(authPresentationMapper::toUserResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(responses));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequest request
    ) {
        UserDto userDto = userApplicationService.updateUser(id, request.getUsername(), request.getPassword());
        UserResponse response = authPresentationMapper.toUserResponse(userDto);

        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable UUID id) {
        userApplicationService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>("User deleted successfully"));
    }
}