package com.taskify.user.service;

import com.taskify.user.dto.organization.*;
import com.taskify.user.entity.Organization;
import com.taskify.user.entity.OrganizationRole;
import com.taskify.user.exception.OrganizationNotFoundException;
import com.taskify.user.exception.ResourceNotFoundException;
import com.taskify.user.mapper.OrganizationMapper;
import com.taskify.user.repository.OrganizationRepository;
import com.taskify.user.repository.OrganizationRoleRepository;
import com.taskify.user.repository.UserOrganizationRepository;
import com.taskify.user.repository.UserRepository;
import com.taskify.user.specification.OrganizationSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrganizationService {
    private final OrganizationRepository _organizationRepository;
    private final OrganizationMapper _organizationMapper;
    private final UserRepository _userRepository;
    private final OrganizationRoleRepository _organizationRoleRepository;

    @Autowired
    public OrganizationService(
            OrganizationRepository organizationRepository,
            OrganizationMapper organizationMapper,
            UserRepository userRepository,
            OrganizationRoleRepository organizationRoleRepository
    ) {
        this._organizationRepository = organizationRepository;
        this._organizationMapper = organizationMapper;
        this._userRepository = userRepository;
        _organizationRoleRepository = organizationRoleRepository;
    }

    public Page<Organization> getAllOrganizations(OrganizationCollectionRequest filter) {
        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(
                        filter.getSortDirection().equalsIgnoreCase("asc")
                                ? Sort.Direction.ASC
                                : Sort.Direction.DESC,
                        filter.getSortBy()
                )
        );

        return _organizationRepository.findAll(
                OrganizationSpecifications.withFilters(filter),
                pageable
        );
    }

    @Transactional
    public Organization createOrganization(CreateOrganizationDto createOrganizationDto) {
        Organization organization = _organizationMapper.toEntity(createOrganizationDto);
        organization = _organizationRepository.save(organization);


        return organization;
    }


    public Organization getOrganizationById(UUID id) {
        return _organizationRepository.findById(id).orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));
    }

    @Transactional
    public Organization updateOrganization(UUID id, UpdateOrganizationDto updateOrganizationDto) {
        Organization organization = this.getOrganizationById(id);

        _organizationMapper.updateEntity(organization, updateOrganizationDto);
        return _organizationRepository.save(organization);
    }

    @Transactional
    public boolean deleteOrganization(UUID id) {
        Organization organization = this.getOrganizationById(id);

        _organizationRepository.deleteById(id);
        return true;
    }

    public OrganizationRole getDefaultRole() {
        return _organizationRoleRepository.getOrganizationRoleByDefault(true);
    }

    public OrganizationRole getRoleOrThrow(UUID roleId) {
        return _organizationRoleRepository.findById(roleId).orElseThrow(
                () -> new ResourceNotFoundException("Role not found", "ROLE_NOT_FOUND")
        );
    }
}