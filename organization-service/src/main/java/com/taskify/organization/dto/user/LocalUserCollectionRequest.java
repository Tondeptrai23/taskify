package com.taskify.organization.dto.user;

import com.taskify.common.dto.BaseCollectionRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class LocalUserCollectionRequest extends BaseCollectionRequest {
    private String username;
    private String email;
    private ZonedDateTime createdFrom;
    private ZonedDateTime createdTo;
}