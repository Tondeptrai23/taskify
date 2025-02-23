package com.taskify.api_gateway.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TokenVerificationResponse {
    private String id;
    private String role;
}
