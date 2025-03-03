package com.taskify.organization.controller;

import com.taskify.common.dto.ApiResponse;
import com.taskify.common.dto.ApiCollectionResponse;
import com.taskify.organization.dto.organization.*;
import com.taskify.organization.entity.Organization;
import com.taskify.organization.mapper.OrganizationMapper;
import com.taskify.organization.service.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/orgs")
public class OrganizationController {
    private final OrganizationService organizationService;
    private final OrganizationMapper organizationMapper;

    @Autowired
    public OrganizationController(
            OrganizationService organizationService,
            OrganizationMapper organizationMapper
    ) {
        this.organizationService = organizationService;
        this.organizationMapper = organizationMapper;
    }

    @GetMapping({"/", ""})
    public ResponseEntity<ApiResponse<ApiCollectionResponse<OrganizationDto>>> findAll(
            @RequestHeader("X-User-Id") UUID userId,
            @ModelAttribute OrganizationCollectionRequest filter
    ) {
        filter.setUserId(userId.toString());

        Page<Organization> organizations = organizationService.getAllOrganizations(filter);
        Page<OrganizationDto> organizationDtos = organizations.map(organizationMapper::toDto);

        var response = new ApiResponse<>(ApiCollectionResponse.from(organizationDtos));
        return ResponseEntity.ok(response);
    }

    @PreAuthorize(value = "@organizationService.isOwner(#id, #userId)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationDto>> updateOrganization(
            @PathVariable("id") UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody UpdateOrganizationDto updateOrganizationDto
    ) {
        Organization organization = organizationService.updateOrganization(id, updateOrganizationDto);
        var response = new ApiResponse<>(organizationMapper.toDto(organization));
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@organizationService.isOwner(#id, #userId)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteOrganization(
            @PathVariable("id") UUID id,
            @RequestHeader("X-User-Id") String userId) {
        organizationService.deleteOrganization(id);
        var response = new ApiResponse<>("Organization deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/", ""})
    public ResponseEntity<ApiResponse<OrganizationDto>> createOrganization(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody CreateOrganizationDto createOrganizationDto
    ) {
        Organization organization = organizationService.createOrganization(createOrganizationDto, userId);
        var response = new ApiResponse<>(organizationMapper.toDto(organization));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationDto>> getOrganizationById(
            @PathVariable("id") UUID id) {
        Organization organization = organizationService.getOrganizationById(id);
        var response = new ApiResponse<>(organizationMapper.toDto(organization));
        return ResponseEntity.ok(response);
    }
}