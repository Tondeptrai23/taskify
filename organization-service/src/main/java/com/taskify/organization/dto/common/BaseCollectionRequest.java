package com.taskify.organization.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseCollectionRequest {
    private int page = 0;
    private int size = 10;
    private String sortBy = "createdAt";
    private String sortDirection = "desc";
}