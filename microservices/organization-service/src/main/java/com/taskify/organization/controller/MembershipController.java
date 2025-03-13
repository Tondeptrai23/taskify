package com.taskify.organization.controller;

import com.taskify.commoncore.dto.ApiResponse;
import com.taskify.commoncore.dto.ApiCollectionResponse;
import com.taskify.organization.annotation.RequiresPermissions;
import com.taskify.organization.dto.membership.BatchMemberOperationDto;
import com.taskify.organization.dto.membership.MembershipCollectionRequest;
import com.taskify.organization.dto.membership.MembershipDto;
import com.taskify.organization.entity.Membership;
import com.taskify.organization.mapper.MembershipMapper;
import com.taskify.organization.service.MembershipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/members")
public class MembershipController {
    private final MembershipService _membershipService;
    private final MembershipMapper _membershipMapper;

    @Autowired
    public MembershipController(
            MembershipService membershipService,
            MembershipMapper membershipMapper
    ) {
        _membershipService = membershipService;
        _membershipMapper = membershipMapper;
    }

    @RequiresPermissions(value = {"VIEW_MEMBER"})
    @GetMapping({"", "/"})
    public ResponseEntity<ApiCollectionResponse<MembershipDto>> getOrganizationMembers(
            @RequestHeader("X-Organization-Context") UUID orgId,
            @ModelAttribute MembershipCollectionRequest filter
    ) {
        Page<MembershipDto> membershipDtos = _membershipService.getOrganizationMembers(orgId, filter)
                .map(_membershipMapper::toDto);

        var response = ApiCollectionResponse.from(membershipDtos);
        return ResponseEntity.ok(response);
    }

    @RequiresPermissions(value = {"INVITE_MEMBER"})
    @PostMapping({"", "/"})
    public ResponseEntity<ApiResponse<List<MembershipDto>>> addMembers(
            @RequestHeader("X-Organization-Context") UUID orgId,
            @RequestBody List<UUID> userIds
    ) {
        List<Membership> memberships = _membershipService.addMembers(orgId, userIds);
        var response = new ApiResponse<>(_membershipMapper.toDtoList(memberships));
        return ResponseEntity.ok(response);
    }

    @RequiresPermissions(value = {"REMOVE_MEMBER"})
    @DeleteMapping({"", "/"})
    public ResponseEntity<ApiResponse<String>> removeMembers(
            @RequestHeader("X-Organization-Context") UUID orgId,
            @RequestBody List<UUID> userIds
    ) {
        _membershipService.deactivateMembers(orgId, userIds);
        var response = new ApiResponse<>("Members removed successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping({"", "/"})
    @RequiresPermissions(value = {"UPDATE_MEMBER_ROLE"})
    public ResponseEntity<ApiResponse<List<MembershipDto>>> updateMembers(
            @RequestHeader("X-Organization-Context") UUID orgId,
            @RequestBody BatchMemberOperationDto updateMembersDto
    ) {
        List<Membership> memberships = _membershipService.updateMembers(orgId, updateMembersDto.getMembers(), updateMembersDto.getRoleId());
        var response = new ApiResponse<>(_membershipMapper.toDtoList(memberships));
        return ResponseEntity.ok(response);
    }
}