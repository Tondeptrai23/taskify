package com.taskify.project.workitem.entity;

import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Document(collection = "work-items")
public class WorkItem {
    @Id
    private String id;

    @Field("type")
    private WorkItemType type;

    @Field("title")
    private String title;

    @Field("description")
    private String description;

    @Field("status")
    private WorkItemStatus status;

    @Field("priority")
    private Priority priority;

    @Field("project_id")
    private UUID projectId;

    @Field("parent_id")
    private String parentId;

    @Field("assignee_id")
    private UUID assigneeId;

    @Field("reporter_id")
    private UUID reporterId;

    @Field("severity")
    private Severity severity;

    @Field("status_order")
    private Integer statusOrder;

    @Field("global_order")
    private Integer globalOrder;

    @Field("dependencies")
    private List<WorkItemDependency> dependencies;

    @CreatedDate
    @Field("created_at")
    private ZonedDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private ZonedDateTime updatedAt;

    @Field("created_by")
//    @CreatedBy // TODO: Uncomment this line
    private UUID createdBy;

    @Field("updated_by")
//    @LastModifiedBy // TODO: Uncomment this line
    private UUID updatedBy;

    @Data
    public static class WorkItemDependency {
        @Field("task_id")
        private UUID taskId;

        @Field("type")
        private DependencyType type;

        @Field("created_at")
        private ZonedDateTime createdAt;

        @Field("created_by")
        private UUID createdBy;
    }
}