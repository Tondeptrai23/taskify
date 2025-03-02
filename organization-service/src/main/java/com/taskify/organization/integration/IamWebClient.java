package com.taskify.organization.integration;

import com.taskify.organization.dto.role.OrganizationRoleDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class IamWebClient {
    private final WebClient webClient;

    public IamWebClient(WebClient.Builder webClientBuilder,
                        @Value("${services.iam.url}") String iamServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(iamServiceUrl).build();
    }

    public Mono<OrganizationRoleDto> getOrganizationRoleById(String organizationId, String roleId) {
        return webClient.get()
                .uri("/orgs/roles/{roleId}", organizationId, roleId)
                .header("X-Organization-Context", organizationId)
                .retrieve()
                .bodyToMono(OrganizationRoleDto.class);
    }

    public Mono<OrganizationRoleDto> getDefaultOrganizationRole(String organizationId) {
        return webClient.get()
                .uri("/internal/orgs/roles/default")
                .header("X-Organization-Context", organizationId)
                .retrieve()
                .bodyToMono(OrganizationRoleDto.class);
    }
}
