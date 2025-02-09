package com.taskify.user.controller;

import com.taskify.user.dto.common.BaseCollectionResponse;
import com.taskify.user.dto.organization.*;
import com.taskify.user.entity.Organization;
import com.taskify.user.entity.UserOrganization;
import com.taskify.user.mapper.OrganizationMapper;
import com.taskify.user.mapper.UserOrganizationMapper;
import com.taskify.user.service.OrganizationService;
import com.taskify.user.service.UserOrganizationService;
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
    private final UserOrganizationService userOrganizationService;
    private final OrganizationMapper organizationMapper;
    private final UserOrganizationMapper userOrganizationMapper;

    @Autowired
    public OrganizationController(
            OrganizationService organizationService,
            UserOrganizationService userOrganizationService,
            OrganizationMapper organizationMapper,
            UserOrganizationMapper userOrganizationMapper
    ) {
        this.organizationService = organizationService;
        this.userOrganizationService = userOrganizationService;
        this.organizationMapper = organizationMapper;
        this.userOrganizationMapper = userOrganizationMapper;
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
    public ResponseEntity<String> deleteOrganization(@PathVariable("id") UUID id) {
        organizationService.deleteOrganization(id);

        return ResponseEntity.ok("Organization deleted successfully");
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<BaseCollectionResponse<OrganizationMemberDto>> getOrganizationMembers(
            @PathVariable("id") UUID orgId,
            @ModelAttribute OrganizationMemberCollectionRequest filter
    ) {
        Page<OrganizationMemberDto> userOrganizationDtos = userOrganizationService.getOrganizationMembers(orgId, filter)
                .map(userOrganizationMapper::toDto);

        return ResponseEntity.ok(BaseCollectionResponse.from(userOrganizationDtos));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<List<OrganizationMemberDto>> addMembers(
            @PathVariable("id") UUID orgId,
            @RequestBody List<UUID> userIds
    ) {
        List<UserOrganization> userOrganizations = userOrganizationService.addMembers(orgId, userIds);

        return ResponseEntity.ok(userOrganizationMapper.toDtoList(userOrganizations));
    }

    @DeleteMapping("/{id}/members")
    public ResponseEntity<String> removeMembers(
            @PathVariable("id") UUID orgId,
            @RequestBody List<UUID> userIds
    ) {
        userOrganizationService.deactivateMembers(orgId, userIds);

        return ResponseEntity.ok("Members removed successfully");
    }

    @PutMapping("/{id}/members")
    public ResponseEntity<List<OrganizationMemberDto>> updateMembers(
            @PathVariable("id") UUID orgId,
            @RequestBody BatchMemberOperationDto updateMembersDto
    ) {
        List<UserOrganization> userOrganizations = userOrganizationService.updateMembers(orgId, updateMembersDto.getMembers(), updateMembersDto.getRoleId());

        return ResponseEntity.ok(userOrganizationMapper.toDtoList(userOrganizations));
    }
}