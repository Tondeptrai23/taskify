package com.taskify.project.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.time.ZonedDateTime;
import java.util.Optional;

@Configuration
@EnableMongoAuditing(dateTimeProviderRef = "mongoDateTimeProvider")
public class MongoAuditConfig {
    @Bean
    public DateTimeProvider mongoDateTimeProvider() {
        return () -> Optional.of(ZonedDateTime.now());
    }
}