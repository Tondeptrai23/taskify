package com.taskify.organization.controller;

import com.taskify.organization.dto.common.BaseCollectionResponse;
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
@RequestMapping("/api/v1/organizations")
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
    public ResponseEntity<BaseCollectionResponse<MembershipDto>> getOrganizationMembers(
            @PathVariable("id") UUID orgId,
            @ModelAttribute MembershipCollectionRequest filter
    ) {
        Page<MembershipDto> membershipDtos = membershipService.getOrganizationMembers(orgId, filter)
                .map(membershipMapper::toDto);

        return ResponseEntity.ok(BaseCollectionResponse.from(membershipDtos));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<List<MembershipDto>> addMembers(
            @PathVariable("id") UUID orgId,
            @RequestBody List<UUID> userIds
    ) {
        List<Membership> memberships = membershipService.addMembers(orgId, userIds);

        return ResponseEntity.ok(membershipMapper.toDtoList(memberships));
    }

    @DeleteMapping("/{id}/members")
    public ResponseEntity<String> removeMembers(
            @PathVariable("id") UUID orgId,
            @RequestBody List<UUID> userIds
    ) {
        membershipService.deactivateMembers(orgId, userIds);

        return ResponseEntity.ok("Members removed successfully");
    }

    @PutMapping("/{id}/members")
    public ResponseEntity<List<MembershipDto>> updateMembers(
            @PathVariable("id") UUID orgId,
            @RequestBody BatchMemberOperationDto updateMembersDto
    ) {
        List<Membership> memberships = membershipService.updateMembers(orgId, updateMembersDto.getMembers(), updateMembersDto.getRoleId());

        return ResponseEntity.ok(membershipMapper.toDtoList(memberships));
    }
}
