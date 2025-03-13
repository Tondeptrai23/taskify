package com.taskify.commoncore.constant;

import lombok.Getter;

@Getter
public enum Permission {
    // Project permissions
    CREATE_PROJECT("CREATE_PROJECT"),
    UPDATE_PROJECT("UPDATE_PROJECT"),
    DELETE_PROJECT("DELETE_PROJECT"),
    VIEW_PROJECT("VIEW_PROJECT"),

    // Task permissions
    CREATE_TASK("CREATE_TASK"),
    UPDATE_TASK_STATUS("UPDATE_TASK_STATUS"),
    DELETE_TASK("DELETE_TASK"),
    VIEW_TASK("VIEW_TASK"),

    // Member permissions
    INVITE_MEMBER("INVITE_MEMBER"),
    REMOVE_MEMBER("REMOVE_MEMBER"),
    UPDATE_MEMBER_ROLE("UPDATE_MEMBER_ROLE"),
    VIEW_MEMBER("VIEW_MEMBER"),
    MANAGE_ROLE("MANAGE_ROLE");

    private final String value;

    Permission(String value) {
        this.value = value;
    }
}
