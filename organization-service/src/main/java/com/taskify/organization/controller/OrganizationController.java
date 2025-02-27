package com.taskify.organization.controller;

import com.taskify.common.dto.BaseCollectionResponse;
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
    public ResponseEntity<BaseCollectionResponse<OrganizationDto>> findAll(
            @RequestHeader("X-User-Id") UUID userId,
            @ModelAttribute OrganizationCollectionRequest filter
    ) {
        filter.setUserId(userId.toString());

        Page<Organization> organizations = organizationService.getAllOrganizations(filter);
        Page<OrganizationDto> organizationDtos = organizations.map(organizationMapper::toDto);

        return ResponseEntity.ok(BaseCollectionResponse.from(organizationDtos));
    }

    @PreAuthorize(value = "@organizationService.isOwner(#id, #userId)")
    @PutMapping("/{id}")
    public ResponseEntity<OrganizationDto> updateOrganization(
            @PathVariable("id") UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody UpdateOrganizationDto updateOrganizationDto
    ) {
        Organization organization = organizationService.updateOrganization(id, updateOrganizationDto);

        return ResponseEntity.ok(organizationMapper.toDto(organization));
    }

    @PreAuthorize("@organizationService.isOwner(#id, #userId)")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrganization(
            @PathVariable("id") UUID id,
            @RequestHeader("X-User-Id") String userId) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.ok("Organization deleted successfully");
    }

    @PostMapping({"/", ""})
    public ResponseEntity<OrganizationDto> createOrganization(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody CreateOrganizationDto createOrganizationDto
    ) {
        Organization organization = organizationService.createOrganization(createOrganizationDto, userId);
        return ResponseEntity.ok(organizationMapper.toDto(organization));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDto> getOrganizationById(
            @PathVariable("id") UUID id) {
        Organization organization = organizationService.getOrganizationById(id);

        return ResponseEntity.ok(organizationMapper.toDto(organization));
    }
}

