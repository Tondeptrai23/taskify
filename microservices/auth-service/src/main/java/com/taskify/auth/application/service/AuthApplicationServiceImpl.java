package com.taskify.auth.application.service;

import com.taskify.auth.application.contracts.AuthApplicationService;
import com.taskify.auth.application.dto.AuthResultDto;
import com.taskify.auth.application.dto.LoginDto;
import com.taskify.auth.application.dto.RegisterUserDto;
import com.taskify.auth.application.dto.UserDto;
import com.taskify.auth.application.mapper.UserMapper;
import com.taskify.auth.domain.entity.RefreshToken;
import com.taskify.auth.domain.entity.User;
import com.taskify.auth.domain.exception.TokenValidationException;
import com.taskify.auth.domain.exception.UserExistsException;
import com.taskify.auth.domain.repository.UserRepository;
import com.taskify.auth.domain.service.AuthService;
import com.taskify.auth.domain.service.PasswordEncoder;
import com.taskify.auth.domain.service.TokenService;
import org.springframework.stereotype.Service;

@Service
public class AuthApplicationServiceImpl implements AuthApplicationService {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthApplicationServiceImpl(
            UserRepository userRepository,
            AuthService authService,
            TokenService tokenService,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public AuthResultDto login(LoginDto loginDto) {
        User user = authService.authenticate(loginDto.getUsername(), loginDto.getPassword());
        String accessToken = tokenService.generateAccessToken(user);
        RefreshToken refreshToken = tokenService.generateRefreshToken(user.getId());
        String encodedRefreshToken = tokenService.encodeRefreshTokenForTransmission(refreshToken.getToken());

        AuthResultDto result = AuthResultDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getSystemRole().name())
                .accessToken(accessToken)
                .refreshToken(encodedRefreshToken)
                .build();

        return result;
    }

    @Override
    public UserDto register(RegisterUserDto registerDto) {
        // Check for existing user
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new UserExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new UserExistsException("Email already exists");
        }

        // Create user
        User user = userMapper.toEntity(registerDto);
        user.setPasswordHash(passwordEncoder.encode(registerDto.getPassword()));

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public AuthResultDto refreshToken(String refreshToken) {
        String decodedToken = tokenService.decodeRefreshTokenFromTransmission(refreshToken);
        RefreshToken token = authService.refreshToken(decodedToken);

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new TokenValidationException("User not found for token"));

        String accessToken = tokenService.generateAccessToken(user);
        String encodedRefreshToken = tokenService.encodeRefreshTokenForTransmission(token.getToken());

        AuthResultDto result = AuthResultDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getSystemRole().name())
                .accessToken(accessToken)
                .refreshToken(encodedRefreshToken)
                .build();

        return result;
    }

    @Override
    public UserDto verifyToken(String token) {
        User user = authService.validateToken(token);
        return userMapper.toDto(user);
    }

    @Override
    public void logout(String refreshToken) {
        String decodedToken = tokenService.decodeRefreshTokenFromTransmission(refreshToken);
        authService.revokeToken(decodedToken);
    }
}