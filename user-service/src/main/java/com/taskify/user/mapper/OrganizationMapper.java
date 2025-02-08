package com.taskify.user.mapper;

import com.taskify.user.dto.organization.CreateOrganizationDto;
import com.taskify.user.dto.organization.OrganizationDto;
import com.taskify.user.dto.organization.UpdateOrganizationDto;
import com.taskify.user.entity.Organization;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {
    OrganizationDto toDto(Organization organization);

    Organization toEntity(OrganizationDto organizationDto);

    Organization toEntity(CreateOrganizationDto createOrganizationDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Organization organization, UpdateOrganizationDto updateOrganizationDto);

    List<OrganizationDto> toDtoList(List<Organization> organizations);
}