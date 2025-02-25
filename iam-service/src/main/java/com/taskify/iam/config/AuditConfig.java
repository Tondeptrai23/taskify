package com.taskify.iam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.neo4j.config.EnableNeo4jAuditing;

import java.time.ZonedDateTime;
import java.util.Optional;

@Configuration
@EnableNeo4jAuditing(dateTimeProviderRef = "dateTimeProvider")
public class AuditConfig {
    @Bean
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(ZonedDateTime.now());
    }
}