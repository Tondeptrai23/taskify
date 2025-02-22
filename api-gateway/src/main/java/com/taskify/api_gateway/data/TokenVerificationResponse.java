package com.taskify.api_gateway.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenVerificationResponse {
    private String id;
    private String role;
}
