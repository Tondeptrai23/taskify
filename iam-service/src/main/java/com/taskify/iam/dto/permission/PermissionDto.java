package com.taskify.iam.dto.permission;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PermissionDto {
    private Long id;
    private String name;
    private String description;
    private List<String> prerequisites;
}