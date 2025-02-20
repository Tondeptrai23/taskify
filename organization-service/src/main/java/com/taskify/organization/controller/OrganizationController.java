package com.taskify.organization.controller;

import com.taskify.organization.dto.common.BaseCollectionResponse;
import com.taskify.organization.dto.organization.*;
import com.taskify.organization.entity.Organization;
import com.taskify.organization.mapper.MembershipMapper;
import com.taskify.organization.mapper.OrganizationMapper;
import com.taskify.organization.service.MembershipService;
import com.taskify.organization.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// TODO: validate authorization
@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;
    private final OrganizationMapper organizationMapper;

    @Autowired
    public OrganizationController(
            OrganizationService organizationService,
            OrganizationMapper organizationMapper,
    ) {
        this.organizationService = organizationService;
        this.organizationMapper = organizationMapper;
    }

    @GetMapping
    public ResponseEntity<BaseCollectionResponse<OrganizationDto>> findAll(
            @ModelAttribute OrganizationCollectionRequest filter
    ) {
        Page<Organization> organizations = organizationService.getAllOrganizations(filter);
        Page<OrganizationDto> organizationDtos = organizations.map(organizationMapper::toDto);

        return ResponseEntity.ok(BaseCollectionResponse.from(organizationDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDto> getOrganizationById(@PathVariable("id") UUID id) {
        Organization organization = organizationService.getOrganizationById(id);

        return ResponseEntity.ok(organizationMapper.toDto(organization));
    }

    @PostMapping
    public ResponseEntity<OrganizationDto> createOrganization(
            @RequestBody CreateOrganizationDto createOrganizationDto
    ) {
        // TODO: get current user id from security context
        Organization organization = organizationService.createOrganization(createOrganizationDto, null);
        return ResponseEntity.ok(organizationMapper.toDto(organization));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationDto> updateOrganization(
            @PathVariable("id") UUID id,
            @RequestBody UpdateOrganizationDto updateOrganizationDto
    ) {
        Organization organization = organizationService.updateOrganization(id, updateOrganizationDto);

        return ResponseEntity.ok(organizationMapper.toDto(organization));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrganization(@PathVariable("id") UUID id) {
        organizationService.deleteOrganization(id);

        return ResponseEntity.ok("Organization deleted successfully");
    }
}