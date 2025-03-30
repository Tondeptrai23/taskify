package com.taskify.auth.infrastructure.config;

import com.taskify.auth.domain.contracts.PasswordEncoder;
import com.taskify.auth.domain.contracts.TokenService;
import com.taskify.auth.domain.repository.RefreshTokenRepository;
import com.taskify.auth.domain.repository.UserRepository;
import com.taskify.auth.domain.service.AuthDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public AuthDomainService authService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            TokenService tokenService,
            PasswordEncoder passwordEncoder

    ) {
        return new AuthDomainService(
                userRepository,
                refreshTokenRepository,
                tokenService,
                passwordEncoder
        );
    }
}
