package com.taskify.organization.mapper;

import com.taskify.organization.dto.membership.MembershipDto;
import com.taskify.organization.entity.LocalUser;
import com.taskify.organization.entity.Membership;
import com.taskify.organization.entity.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MembershipMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    MembershipDto toDto(Membership membership);

    List<MembershipDto> toDtoList(List<Membership> memberships);

    default LocalUser userIdToUser(UUID userId) {
        if (userId == null) return null;
        LocalUser user = new LocalUser();
        user.setId(userId);
        return user;
    }

    default Organization organizationIdToOrganization(UUID organizationId) {
        if (organizationId == null) return null;
        Organization organization = new Organization();
        organization.setId(organizationId);
        return organization;
    }
}