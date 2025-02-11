package com.taskify.project.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
    basePackages = "com.taskify.project.workitem.repository"
)
public class MongoConfig {
}
