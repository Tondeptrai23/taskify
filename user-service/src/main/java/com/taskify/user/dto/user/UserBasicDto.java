package com.taskify.user.dto.user;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserBasicDto {
    private String id;
    private String username;
    private String email;
}
