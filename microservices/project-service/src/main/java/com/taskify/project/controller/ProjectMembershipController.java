package com.taskify.project.controller;

import com.taskify.commoncore.constant.Permission;
import com.taskify.commoncore.dto.ApiResponse;
import com.taskify.commoncore.dto.ApiCollectionResponse;
import com.taskify.project.annotation.RequiresPermissions;
import com.taskify.project.dto.membership.BatchMemberDto;
import com.taskify.project.dto.membership.MembershipCollectionRequest;
import com.taskify.project.dto.membership.ProjectMembershipDto;
import com.taskify.project.dto.membership.UpdateBatchMemberDto;
import com.taskify.project.entity.ProjectMembership;
import com.taskify.project.mapper.ProjectMembershipMapper;
import com.taskify.project.service.ProjectMembershipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/projects/{projectId}/members")
public class ProjectMembershipController {
    private final ProjectMembershipService projectMembershipService;
    private final ProjectMembershipMapper projectMembershipMapper;

    @Autowired
    public ProjectMembershipController(
            ProjectMembershipService projectMembershipService,
            ProjectMembershipMapper projectMembershipMapper
    ) {
        this.projectMembershipService = projectMembershipService;
        this.projectMembershipMapper = projectMembershipMapper;
    }

    @RequiresPermissions(value = {Permission.VIEW_MEMBER})
    @GetMapping({"", "/"})
    public ResponseEntity<ApiResponse<ApiCollectionResponse<ProjectMembershipDto>>> getProjectMembers(
            @PathVariable("projectId") UUID projectId,
            @RequestHeader("X-Organization-Context") UUID orgId,
            @ModelAttribute MembershipCollectionRequest filter
    ) {
        Page<ProjectMembershipDto> membershipDtos = projectMembershipService.getProjectMembers(projectId, filter)
                .map(projectMembershipMapper::toDto);

        var response = new ApiResponse<>(ApiCollectionResponse.from(membershipDtos));
        return ResponseEntity.ok(response);
    }

    @RequiresPermissions(value = {Permission.INVITE_MEMBER})
    @PostMapping({"", "/"})
    public ResponseEntity<ApiResponse<List<ProjectMembershipDto>>> addMembers(
            @PathVariable("projectId") UUID projectId,
            @RequestHeader("X-Organization-Context") UUID orgId,
            @RequestBody BatchMemberDto request
    ) {
        List<ProjectMembership> memberships = projectMembershipService.addMembers(projectId, request.getMembers(), orgId);
        var response = new ApiResponse<>(projectMembershipMapper.toDtoList(memberships));
        return ResponseEntity.ok(response);
    }

    @RequiresPermissions(value = {Permission.REMOVE_MEMBER})
    @DeleteMapping({"", "/"})
    public ResponseEntity<ApiResponse<String>> removeMembers(
            @PathVariable("projectId") UUID projectId,
            @RequestHeader("X-Organization-Context") UUID orgId,
            @RequestBody BatchMemberDto request
    ) {
        projectMembershipService.deactivateMembers(projectId, request.getMembers(), orgId);
        var response = new ApiResponse<>("Members removed successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping({"", "/"})
    @RequiresPermissions(value = {Permission.UPDATE_MEMBER_ROLE})
    public ResponseEntity<ApiResponse<List<ProjectMembershipDto>>> updateMembers(
            @PathVariable("projectId") UUID projectId,
            @RequestHeader("X-Organization-Context") UUID orgId,
            @RequestBody UpdateBatchMemberDto updateMembersDto
    ) {
        List<ProjectMembership> memberships = projectMembershipService.updateMembers(
                projectId,
                updateMembersDto.getMembers(),
                updateMembersDto.getRoleId(),
                orgId
        );
        var response = new ApiResponse<>(projectMembershipMapper.toDtoList(memberships));
        return ResponseEntity.ok(response);
    }
}