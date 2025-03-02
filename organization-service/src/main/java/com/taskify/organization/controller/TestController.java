package com.taskify.organization.controller;

import com.taskify.organization.dto.role.OrganizationRoleDto;
import com.taskify.organization.integration.IamServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/test")
public class TestController {

    private final IamServiceClient iamServiceClient;

    @Autowired
    public TestController(IamServiceClient iamServiceClient) {
        this.iamServiceClient = iamServiceClient;
    }

    @GetMapping("/circuit-breaker")
    public ResponseEntity<OrganizationRoleDto> testCircuitBreaker(
            @RequestParam UUID organizationId,
            @RequestParam(required = false, defaultValue = "false") boolean simulateFail) {

        if (simulateFail) {
            throw new RuntimeException("Simulated failure for testing");
        }

        return ResponseEntity.ok(iamServiceClient.getDefaultOrganizationRole(organizationId));
    }
}