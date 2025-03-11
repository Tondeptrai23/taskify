package com.taskify.organization.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskify.commoncore.dto.ApiError;
import com.taskify.commoncore.error.CommonErrorCode;
import com.taskify.commoncore.error.integration.CircuitBreakerException;
import com.taskify.commoncore.error.integration.ServiceUnavailableException;
import com.taskify.commoncore.error.integration.TimeoutException;
import com.taskify.commoncore.error.resource.OrganizationNotFoundException;
import com.taskify.commoncore.error.resource.RoleNotFoundException;
import com.taskify.commoncore.error.exception.IntegrationException;
import com.taskify.commoncore.error.exception.TaskifyException;
import com.taskify.organization.dto.role.OrganizationRoleDto;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.UUID;

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
        log.error("Failed to get default organization role: {}", ex.getClass().getSimpleName());

        if (ex instanceof CallNotPermittedException) {
            throw new CircuitBreakerException("Circuit breaker is open for IAM service");
        } else {
            throw ex;
        }
    }

    private OrganizationRoleDto getOrganizationRoleByIdFallback(UUID organizationId, UUID roleId, Throwable ex) throws Throwable {
        log.error("Failed to get organization role by id: {}", ex.getClass().getSimpleName());

        if (ex instanceof CallNotPermittedException) {
            throw new CircuitBreakerException("Circuit breaker is open for IAM service");
        } else {
            throw ex;
        }
    }

    private TaskifyException handleIamServiceError(Throwable ex) {
        log.error("Error occurred while communicating with IAM service: {}", ex.getClass().getName());

        if (ex instanceof WebClientResponseException) {
            WebClientResponseException wcException = (WebClientResponseException) ex;

            // For server errors, throw ServiceUnavailableException which should trip the circuit breaker
            if (wcException.getStatusCode().is5xxServerError()) {
                return new ServiceUnavailableException("IAM service server error: " + wcException.getMessage());
            } else {
                return translateErrorResponse(wcException);
            }
        } else if (ex instanceof java.util.concurrent.TimeoutException) {
            return new TimeoutException("Timeout while calling IAM service");
        } else {
            return new IntegrationException("Unexpected error from IAM service", ex);
        }
    }

    private TaskifyException translateErrorResponse(WebClientResponseException ex) {
        try {
            ApiError apiError = objectMapper.readValue(
                    ex.getResponseBodyAsString(),
                    ApiError.class
            );

            String errorCode = apiError.getCode();

            // Match error codes to specific exceptions
            if (CommonErrorCode.ROLE_NOT_FOUND.getCode().equals(errorCode)) {
                return new RoleNotFoundException(apiError.getMessage());
            } else if (CommonErrorCode.ORG_NOT_FOUND.getCode().equals(errorCode)) {
                return new OrganizationNotFoundException(apiError.getMessage());
            } else {
                // For other error codes, create a generic IntegrationException
                return new IntegrationException(
                        "IAM service error: " + apiError.getMessage(),
                        ex
                );
            }
        } catch (JsonProcessingException e) {
            return new IntegrationException(
                    "Unexpected IAM service response format: " + ex.getStatusText(),
                    e
            );
        }
    }
}