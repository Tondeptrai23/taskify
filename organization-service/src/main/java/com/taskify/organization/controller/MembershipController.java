package com.taskify.organization.controller;

import com.taskify.commoncore.dto.ApiResponse;
import com.taskify.commoncore.dto.ApiCollectionResponse;
import com.taskify.organization.dto.membership.BatchMemberOperationDto;
import com.taskify.organization.dto.membership.MembershipCollectionRequest;
import com.taskify.organization.dto.membership.MembershipDto;
import com.taskify.organization.entity.Membership;
import com.taskify.organization.mapper.MembershipMapper;
import com.taskify.organization.service.MembershipService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orgs")
public class MembershipController {
    private final MembershipService membershipService;
    private final MembershipMapper membershipMapper;

    public MembershipController(
            MembershipService membershipService,
            MembershipMapper membershipMapper
    ) {
        this.membershipService = membershipService;
        this.membershipMapper = membershipMapper;
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<ApiCollectionResponse<MembershipDto>>> getOrganizationMembers(
            @PathVariable("id") UUID orgId,
            @ModelAttribute MembershipCollectionRequest filter
    ) {
        Page<MembershipDto> membershipDtos = membershipService.getOrganizationMembers(orgId, filter)
                .map(membershipMapper::toDto);

        var response = new ApiResponse<>(ApiCollectionResponse.from(membershipDtos));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<MembershipDto>>> addMembers(
            @PathVariable("id") UUID orgId,
            @RequestBody List<UUID> userIds
    ) {
        List<Membership> memberships = membershipService.addMembers(orgId, userIds);
        var response = new ApiResponse<>(membershipMapper.toDtoList(memberships));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/members")
    public ResponseEntity<ApiResponse<String>> removeMembers(
            @PathVariable("id") UUID orgId,
            @RequestBody List<UUID> userIds
    ) {
        membershipService.deactivateMembers(orgId, userIds);
        var response = new ApiResponse<>("Members removed successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<MembershipDto>>> updateMembers(
            @PathVariable("id") UUID orgId,
            @RequestBody BatchMemberOperationDto updateMembersDto
    ) {
        List<Membership> memberships = membershipService.updateMembers(orgId, updateMembersDto.getMembers(), updateMembersDto.getRoleId());
        var response = new ApiResponse<>(membershipMapper.toDtoList(memberships));
        return ResponseEntity.ok(response);
    }
}