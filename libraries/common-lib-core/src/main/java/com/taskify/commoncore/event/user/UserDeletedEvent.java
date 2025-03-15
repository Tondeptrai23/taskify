package com.taskify.commoncore.event.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserDeletedEvent {
    private UUID id;
}
