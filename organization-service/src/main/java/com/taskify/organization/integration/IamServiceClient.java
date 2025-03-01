package com.taskify.organization.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskify.common.dto.ErrorResponse;
import com.taskify.common.error.BusinessException;
import com.taskify.common.error.OrganizationNotFoundException;
import com.taskify.common.error.RoleNotFoundException;
import com.taskify.common.error.ServiceIntegrationException;
import com.taskify.organization.dto.role.OrganizationRoleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Service
public class IamServiceClient {
    private final IamWebClient iamWebClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public IamServiceClient(IamWebClient iamWebClient, ObjectMapper objectMapper) {
        this.iamWebClient = iamWebClient;
        this.objectMapper = objectMapper;
    }

    public OrganizationRoleDto getDefaultOrganizationRole(UUID organizationId) {
        try {
            return iamWebClient
                    .getDefaultOrganizationRole(organizationId.toString())
                    .onErrorMap(this::handleIamServiceError)
                    .block();
        } catch (Exception ex) {
            throw handleIamServiceError(ex);
        }
    }

    public OrganizationRoleDto getOrganizationRoleById(UUID organizationId, UUID roleId) {
        try {
            return iamWebClient
                    .getOrganizationRoleById(organizationId.toString(), roleId.toString())
                    .onErrorMap(this::handleIamServiceError)
                    .block();
        } catch (Exception ex) {
            throw handleIamServiceError(ex);
        }
    }

    private BusinessException handleIamServiceError(Throwable ex) {
        if (ex instanceof WebClientResponseException) {
            WebClientResponseException wcException = (WebClientResponseException) ex;
            return translateErrorResponse(wcException);
        } else {
            return new ServiceIntegrationException("IAM service error: " + ex.getMessage(), ex);
        }
    }

    private BusinessException translateErrorResponse(WebClientResponseException ex) {
        try {
            ErrorResponse errorResponse = objectMapper.readValue(
                    ex.getResponseBodyAsString(),
                    ErrorResponse.class
            );

            switch (errorResponse.getErrorCode()) {
                case "ROLE_NOT_FOUND":
                    return new RoleNotFoundException(errorResponse.getMessage());
                case "ORG_NOT_FOUND":
                    return new OrganizationNotFoundException(errorResponse.getMessage());
                default:
                    return new ServiceIntegrationException(
                            "IAM service error: " + errorResponse.getMessage(),
                            errorResponse.getErrorCode()
                    );
            }
        } catch (JsonProcessingException e) {
            return new ServiceIntegrationException(
                    "Unexpected IAM service response: " + ex.getStatusText(),
                    ex
            );
        }
    }
}