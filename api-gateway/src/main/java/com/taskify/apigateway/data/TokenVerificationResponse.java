package com.taskify.apigateway.data;

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
