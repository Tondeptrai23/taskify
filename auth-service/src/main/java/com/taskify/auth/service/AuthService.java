package com.taskify.auth.service;

import com.taskify.auth.dto.auth.AuthTokens;
import com.taskify.auth.dto.auth.RegisterRequest;
import com.taskify.auth.dto.user.CreateUserDto;
import com.taskify.auth.entity.User;
import com.taskify.auth.exception.EmailAlreadyExistsException;
import com.taskify.auth.exception.InvalidCredentialException;
import com.taskify.common.error.exception.UnauthorizedException;
import com.taskify.auth.exception.UsernameAlreadyExistsException;
import com.taskify.auth.mapper.UserMapper;
import com.taskify.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class AuthService {
    private final UserRepository _userRepository;
    private final JwtService _jwtService;
    private final RefreshTokenService _refreshTokenService;
    private final UserService _userService;
    private final PasswordEncoder _passwordEncoder;
    private final UserMapper _userMapper;

    @Autowired
    public AuthService(JwtService jwtService,
                       RefreshTokenService refreshTokenService,
                       UserRepository userRepository,
                       UserService userService,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper) {
        this._jwtService = jwtService;
        this._refreshTokenService = refreshTokenService;
        this._userRepository = userRepository;
        this._userService = userService;
        this._passwordEncoder = passwordEncoder;
        this._userMapper = userMapper;
    }

    public Pair<User, AuthTokens> login(String username, String password) {
        User user = _userRepository.findUserByUsername(username).orElseThrow(
                () -> new InvalidCredentialException("Invalid username")
        );

        var userPassword = user.getPasswordHash();
        if (!_passwordEncoder.matches(password, userPassword)) {
            throw new InvalidCredentialException("Invalid password");
        }

        String token = _jwtService.generateToken(user);
        String refreshToken = _refreshTokenService.generateToken(user.getId());
        return Pair.of(user, new AuthTokens(token, refreshToken));
    }

    @Transactional
    public User registerUser(RegisterRequest request) {
        // Check for existing username
        if (_userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        // Check for existing email
        if (_userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        var dto = new CreateUserDto(request.getUsername(), request.getEmail(), request.getPassword(), "USER");
        return _userService.createUser(dto);
    }

    public User verify(String token) {
        var tokenParts = token.split(" ");

        var claims = _jwtService.getClaims(tokenParts[1]);

        return _userRepository.findUserById(UUID.fromString(claims.getSubject()))
                .orElseThrow(() -> new UnauthorizedException("Invalid token"));
    }

    @Transactional
    public AuthTokens refresh(String token) {
        var hashedToken = _refreshTokenService.findTokenByBase64Token(token);

        User user = _userRepository.findUserById(hashedToken.getUserId())
                .orElseThrow(() -> new UnauthorizedException("Invalid token"));

        _refreshTokenService.verifyToken(hashedToken.getToken());

        var newRefreshToken = _refreshTokenService.generateToken(hashedToken.getUserId());
        var newAccessToken = _jwtService.generateToken(user);

        _refreshTokenService.revokeToken(hashedToken.getToken());

        return new AuthTokens(newAccessToken, newRefreshToken);
    }
}
