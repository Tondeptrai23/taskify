package com.taskify.auth.presentation.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyResponse {
    private String id;
    private String username;
    private String email;
    private String organizationId;
}
