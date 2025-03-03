package com.taskify.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse <T> {
    private boolean success;
    private T data;

    public ApiResponse(T data) {
        this.success = true;
        this.data = data;
    }
}
