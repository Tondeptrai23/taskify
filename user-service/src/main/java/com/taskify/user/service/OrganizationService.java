package com.taskify.user.service;

import com.taskify.user.dto.organization.*;
import com.taskify.user.entity.Organization;
import com.taskify.user.entity.User;
import com.taskify.user.exception.OrganizationNotFoundException;
import com.taskify.user.mapper.OrganizationMapper;
import com.taskify.user.repository.OrganizationRepository;
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
    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;
    private final UserRepository userRepository;

    @Autowired
    public OrganizationService(
            OrganizationRepository organizationRepository,
            OrganizationMapper organizationMapper,
            UserRepository userRepository
    ) {
        this.organizationRepository = organizationRepository;
        this.organizationMapper = organizationMapper;
        this.userRepository = userRepository;
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

        return organizationRepository.findAll(
                OrganizationSpecifications.withFilters(filter),
                pageable
        );
    }

    @Transactional
    public Organization createOrganization(CreateOrganizationDto createOrganizationDto) {
        Organization organization = organizationMapper.toEntity(createOrganizationDto);
        organization = organizationRepository.save(organization);

        return organization;
    }


    public Organization getOrganizationById(UUID id) {
        return organizationRepository.findById(id).orElse(null);
    }

    @Transactional
    public Organization updateOrganization(UUID id, UpdateOrganizationDto updateOrganizationDto) {
        Organization organization = this.getOrganizationById(id);
        if (organization == null) {
            return null;
        }

        organizationMapper.updateEntity(organization, updateOrganizationDto);
        return organizationRepository.save(organization);
    }

    @Transactional
    public Organization deleteOrganization(UUID id) {
        Organization organization = this.getOrganizationById(id);
        if (organization == null) {
            throw new OrganizationNotFoundException("Organization not found");
        }

        organizationRepository.deleteById(id);
        return organization;
    }
}