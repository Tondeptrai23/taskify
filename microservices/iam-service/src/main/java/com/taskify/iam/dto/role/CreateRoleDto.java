package com.taskify.iam.dto.role;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateRoleDto {
    private String name;
    private String description;
    private List<String> permissions;
}