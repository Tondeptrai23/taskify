package com.taskify.project.workitem.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Document(collection = "work-item-history")
public class WorkItemHistory {
    @Id
    private String id;

    @Field("workitem_id")
    private String workitemId;

    @Field("from")
    private String from;

    @Field("to")
    private String to;

    @Field("changed_by")
    @CreatedBy
    private UUID changedBy;

    @Field("changed_at")
    @CreatedDate
    private ZonedDateTime changedAt;

    @Field("reason")
    private String reason;
}