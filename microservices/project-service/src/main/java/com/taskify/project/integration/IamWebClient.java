package com.taskify.project.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskify.commoncore.dto.ApiResponse;
import com.taskify.project.dto.project.ProjectRoleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class IamWebClient {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public IamWebClient(WebClient.Builder webClientBuilder,
                        ObjectMapper objectMapper,
                        @Value("${services.iam.url}") String iamServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(iamServiceUrl).build();
        this.objectMapper = objectMapper;
    }

    public Mono<ProjectRoleDto> getProjectRoleById(String projectId, String roleId) {
        return webClient.get()
                .uri("/context/{projectId}/roles/{roleId}", projectId, roleId)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(response -> {
                    if (response.getData() == null) {
                        return null;
                    }
                    return objectMapper.convertValue(response.getData(), ProjectRoleDto.class);
                });
    }

    public Mono<ProjectRoleDto> getDefaultProjectRole(String projectId) {
        return webClient.get()
                .uri("/internal/context/{projectId}/roles/default", projectId)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(response -> {
                    if (response.getData() == null) {
                        return null;
                    }
                    return objectMapper.convertValue(response.getData(), ProjectRoleDto.class);
                });
    }
}
