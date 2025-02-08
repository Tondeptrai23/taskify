package com.taskify.user.controller;

import com.taskify.user.dto.common.BaseCollectionResponse;
import com.taskify.user.dto.organization.*;
import com.taskify.user.entity.Organization;
import com.taskify.user.mapper.OrganizationMapper;
import com.taskify.user.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations")
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
        Organization organization = organizationService.createOrganization(createOrganizationDto);
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
    public ResponseEntity<OrganizationDto> deleteOrganization(@PathVariable("id") UUID id) {
        Organization organization = organizationService.deleteOrganization(id);

        return ResponseEntity.ok(organizationMapper.toDto(organization));
    }
}