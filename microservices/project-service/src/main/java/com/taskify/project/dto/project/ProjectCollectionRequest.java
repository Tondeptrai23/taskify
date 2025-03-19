package com.taskify.project.dto.project;

import com.taskify.commoncore.dto.BaseCollectionRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;

@Getter
@Setter
public class ProjectCollectionRequest extends BaseCollectionRequest {
    private String name;
    private String key;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime createdFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime createdTo;
}