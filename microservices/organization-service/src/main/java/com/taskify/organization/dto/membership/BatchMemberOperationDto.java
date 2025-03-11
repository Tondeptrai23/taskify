package com.taskify.organization.dto.membership;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class BatchMemberOperationDto {
    private List<UUID> members;
    private UUID roleId;
}