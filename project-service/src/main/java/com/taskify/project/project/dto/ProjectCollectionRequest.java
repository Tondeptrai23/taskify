package com.taskify.project.project.dto;

import com.taskify.project.common.dto.BaseCollectionRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ProjectCollectionRequest extends BaseCollectionRequest {
    private String name;
    private List<String> statuses;
    private UUID organizationId;
    private ZonedDateTime createdFrom;
    private ZonedDateTime createdTo;
}
