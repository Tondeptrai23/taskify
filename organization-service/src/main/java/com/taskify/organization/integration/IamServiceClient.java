package com.taskify.organization.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskify.common.dto.ErrorResponse;
import com.taskify.common.error.*;
import com.taskify.organization.dto.role.OrganizationRoleDto;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class IamServiceClient {
    private final IamWebClient iamWebClient;
    private final ObjectMapper objectMapper;

    private static final Long DEFAULT_TIMEOUT_IN_SECONDS = 2L;

    @Autowired
    public IamServiceClient(IamWebClient iamWebClient, ObjectMapper objectMapper) {
        this.iamWebClient = iamWebClient;
        this.objectMapper = objectMapper;
    }

    @CircuitBreaker(name = "${services.iam.name}", fallbackMethod = "getDefaultOrganizationRoleFallback")
    public OrganizationRoleDto getDefaultOrganizationRole(UUID organizationId) {
        return iamWebClient
                .getDefaultOrganizationRole(organizationId.toString())
                .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_IN_SECONDS))
                .onErrorMap(this::handleIamServiceError)
                .block();
    }

    @CircuitBreaker(name = "${services.iam.name}", fallbackMethod = "getOrganizationRoleByIdFallback")
    public OrganizationRoleDto getOrganizationRoleById(UUID organizationId, UUID roleId) {
        return iamWebClient
                .getOrganizationRoleById(organizationId.toString(), roleId.toString())
                .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_IN_SECONDS))
                .onErrorMap(this::handleIamServiceError)
                .block();
    }

    private OrganizationRoleDto getDefaultOrganizationRoleFallback(UUID organizationId, Throwable ex) throws Throwable {
        log.error("Failed to default organization role " +ex.getClass().getSimpleName());

         if (ex instanceof CallNotPermittedException) {
             throw new CircuitBreakerOpenedException(ex);
        } else {
            throw ex;
        }
    }

    private OrganizationRoleDto getOrganizationRoleByIdFallback(UUID organizationId, UUID roleId, Throwable ex) throws Throwable {
        log.error("Failed to get organization role by id " +ex.getClass().getSimpleName());

        if (ex instanceof CallNotPermittedException) {
            throw new CircuitBreakerOpenedException(ex);
        } else {
            throw ex;
        }
    }

    private BusinessException handleIamServiceError(Throwable ex) {
        log.error("Failed to get organization role by id " +ex.getClass());

        if (ex instanceof WebClientResponseException) {
            WebClientResponseException wcException = (WebClientResponseException) ex;

            // For server errors, throw ServiceIntegrationException which should trip the circuit breaker
            if (wcException.getStatusCode().is5xxServerError()) {
                return new ServiceIntegrationException(
                        "IAM service server error: " + wcException.getMessage(),
                        "SERVER_ERROR",
                        ex);
            } else {
                return translateErrorResponse(wcException);
            }
        } else if (ex instanceof TimeoutException) {
            return new ServiceIntegrationException("Timeout while calling IAM service", "TIME_OUT", ex);
        }
        else {
            return new ServiceIntegrationException("Unexpected error from IAM service", ex);
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
                    return new BusinessException(
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