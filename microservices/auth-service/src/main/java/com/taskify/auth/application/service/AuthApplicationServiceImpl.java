package com.taskify.auth.application.service;

import com.taskify.auth.application.aop.TranslateDomainException;
import com.taskify.auth.application.contracts.AuthApplicationService;
import com.taskify.auth.application.contracts.UserEventPublisher;
import com.taskify.auth.application.dto.AuthResultDto;
import com.taskify.auth.application.dto.LoginDto;
import com.taskify.auth.application.dto.RegisterUserDto;
import com.taskify.auth.application.dto.UserDto;
import com.taskify.auth.application.exception.EmailExistsException;
import com.taskify.auth.application.exception.UserNotFoundException;
import com.taskify.auth.application.exception.UsernameExistsException;
import com.taskify.auth.application.mapper.UserMapper;
import com.taskify.auth.domain.entity.RefreshToken;
import com.taskify.auth.domain.entity.User;
import com.taskify.auth.domain.repository.UserRepository;
import com.taskify.auth.domain.service.AuthDomainService;
import com.taskify.auth.domain.contracts.PasswordEncoder;
import com.taskify.auth.domain.contracts.TokenService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AuthApplicationServiceImpl implements AuthApplicationService {
    private final UserRepository userRepository;
    private final AuthDomainService authDomainService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserEventPublisher userEventPublisher;

    public AuthApplicationServiceImpl(
            UserRepository userRepository,
            AuthDomainService authService,
            TokenService tokenService,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            UserEventPublisher userEventPublisher
    ) {
        this.userRepository = userRepository;
        this.authDomainService = authService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.userEventPublisher = userEventPublisher;
    }

    @Override
    public AuthResultDto login(LoginDto loginDto) {
        User user = authDomainService.authenticate(loginDto.getUsername(), loginDto.getPassword());
        String accessToken = tokenService.generateAccessToken(user);
        RefreshToken refreshToken = tokenService.generateRefreshToken(user.getId());

        String encodedRefreshToken = tokenService.encodeRefreshTokenForTransmission(refreshToken.getRawToken());

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
    @Transactional
    @TranslateDomainException
    public UserDto register(RegisterUserDto registerDto) {
        // Check for existing user
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new UsernameExistsException(registerDto.getUsername());
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new EmailExistsException(registerDto.getEmail());
        }

        // Create user
        User user = userMapper.toEntity(registerDto);
        user.setPasswordHash(passwordEncoder.encode(registerDto.getPassword()));

        User savedUser = userRepository.save(user);

        // Publish user created event
        userEventPublisher.publishUserCreatedEvent(savedUser);

        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    @TranslateDomainException
    public AuthResultDto refreshToken(String refreshToken) {
        String decodedToken = tokenService.decodeRefreshTokenFromTransmission(refreshToken);

        String storedToken = tokenService.hashTokenForStorage(decodedToken);

        RefreshToken token = authDomainService.refreshToken(storedToken);

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String accessToken = tokenService.generateAccessToken(user);
        String encodedRefreshToken = tokenService.encodeRefreshTokenForTransmission(token.getRawToken());

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
    @Transactional
    @TranslateDomainException
    public UserDto verifyToken(String token) {
        User user = authDomainService.validateToken(token);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    @TranslateDomainException
    public void logout(String refreshToken) {
        String decodedToken = tokenService.decodeRefreshTokenFromTransmission(refreshToken);
        String hashedToken = tokenService.hashTokenForStorage(decodedToken);
        authDomainService.revokeToken(hashedToken);
    }
}