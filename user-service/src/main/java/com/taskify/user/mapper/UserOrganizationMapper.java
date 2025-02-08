package com.taskify.user.mapper;

import com.taskify.user.dto.organization.*;
import com.taskify.user.entity.User;
import com.taskify.user.entity.Organization;
import com.taskify.user.entity.OrganizationRole;
import com.taskify.user.entity.UserOrganization;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserOrganizationMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "roleName", source = "role.name")
    OrganizationMemberDto toDto(UserOrganization userOrganization);

    List<OrganizationMemberDto> toDtoList(List<UserOrganization> userOrganizations);

    default User userIdToUser(String userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(UUID.fromString(userId));
        return user;
    }

    default OrganizationRole roleIdToRole(String roleId) {
        if (roleId == null) return null;
        OrganizationRole role = new OrganizationRole();
        role.setId(UUID.fromString(roleId));
        return role;
    }
}