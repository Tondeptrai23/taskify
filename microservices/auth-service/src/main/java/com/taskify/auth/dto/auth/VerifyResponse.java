package com.taskify.auth.dto.auth;

import com.taskify.auth.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyResponse {
    private String id;
    private String username;
    private String email;
    private String organizationId;

    public static VerifyResponse fromUser(User user) {
        var response = new VerifyResponse();
        response.setId(user.getId().toString());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        return response;
    }

    public static VerifyResponse fromUser(User user, String organizationId) {
        var response = fromUser(user);
        response.setOrganizationId(organizationId);
        return response;
    }
}
