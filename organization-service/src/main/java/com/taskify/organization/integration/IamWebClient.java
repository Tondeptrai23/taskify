package com.taskify.organization.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskify.commoncore.dto.ApiResponse;
import com.taskify.organization.dto.role.OrganizationRoleDto;
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

    public Mono<OrganizationRoleDto> getOrganizationRoleById(String organizationId, String roleId) {
        return webClient.get()
                .uri("/orgs/roles/{roleId}", organizationId, roleId)
                .header("X-Organization-Context", organizationId)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(response -> {
                    if (response.getData() == null) {
                        return null;
                    }
                    return objectMapper.convertValue(response.getData(), OrganizationRoleDto.class);
                });
    }

    public Mono<OrganizationRoleDto> getDefaultOrganizationRole(String organizationId) {
        return webClient.get()
                .uri("/internal/orgs/roles/default")
                .header("X-Organization-Context", organizationId)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(response -> {
                    if (response.getData() == null) {
                        return null;
                    }
                    return objectMapper.convertValue(response.getData(), OrganizationRoleDto.class);
                });
    }
}
