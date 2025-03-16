package com.taskify.organization.controller;

import com.taskify.commoncore.constant.Permission;
import com.taskify.commoncore.dto.ApiResponse;
import com.taskify.commoncore.dto.ApiCollectionResponse;
import com.taskify.organization.annotation.RequiresPermissions;
import com.taskify.organization.dto.membership.BatchMemberDto;
import com.taskify.organization.dto.membership.UpdateBatchMemberDto;
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

    @RequiresPermissions(value = {Permission.VIEW_MEMBER})
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

    @RequiresPermissions(value = {Permission.INVITE_MEMBER})
    @PostMapping({"", "/"})
    public ResponseEntity<ApiResponse<List<MembershipDto>>> addMembers(
            @RequestHeader("X-Organization-Context") UUID orgId,
            @RequestBody BatchMemberDto request
    ) {
        List<Membership> memberships = _membershipService.addMembers(orgId, request.getMembers());
        var response = new ApiResponse<>(_membershipMapper.toDtoList(memberships));
        return ResponseEntity.ok(response);
    }

    @RequiresPermissions(value = {Permission.REMOVE_MEMBER})
    @DeleteMapping({"", "/"})
    public ResponseEntity<ApiResponse<String>> removeMembers(
            @RequestHeader("X-Organization-Context") UUID orgId,
            @RequestBody BatchMemberDto request
            ) {
        _membershipService.deactivateMembers(orgId, request.getMembers());
        var response = new ApiResponse<>("Members removed successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping({"", "/"})
    @RequiresPermissions(value = {Permission.UPDATE_MEMBER_ROLE})
    public ResponseEntity<ApiResponse<List<MembershipDto>>> updateMembers(
            @RequestHeader("X-Organization-Context") UUID orgId,
            @RequestBody UpdateBatchMemberDto updateMembersDto
    ) {
        List<Membership> memberships = _membershipService.updateMembers(orgId, updateMembersDto.getMembers(), updateMembersDto.getRoleId());
        var response = new ApiResponse<>(_membershipMapper.toDtoList(memberships));
        return ResponseEntity.ok(response);
    }
}