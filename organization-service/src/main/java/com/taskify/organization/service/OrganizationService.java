package com.taskify.organization.service;

import com.taskify.organization.dto.organization.CreateOrganizationDto;
import com.taskify.organization.dto.organization.OrganizationCollectionRequest;
import com.taskify.organization.dto.organization.UpdateOrganizationDto;
import com.taskify.organization.entity.LocalUser;
import com.taskify.organization.entity.Membership;
import com.taskify.organization.entity.Organization;
import com.taskify.organization.exception.OrganizationNotFoundException;
import com.taskify.organization.exception.ResourceNotFoundException;
import com.taskify.organization.mapper.OrganizationMapper;
import com.taskify.organization.repository.LocalUserRepository;
import com.taskify.organization.repository.MembershipRepository;
import com.taskify.organization.repository.OrganizationRepository;
import com.taskify.organization.specification.OrganizationSpecifications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final LocalUserRepository localUserRepository;
    private final MembershipRepository membershipRepository;
    private final OrganizationMapper organizationMapper;

    @Autowired
    public OrganizationService(
            OrganizationRepository organizationRepository,
            LocalUserRepository localUserRepository,
            MembershipRepository membershipRepository,
            OrganizationMapper organizationMapper
    ) {
        this.organizationRepository = organizationRepository;
        this.localUserRepository = localUserRepository;
        this.membershipRepository = membershipRepository;
        this.organizationMapper = organizationMapper;
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
    public Organization createOrganization(CreateOrganizationDto createOrganizationDto, UUID ownerId) {
        // Ensure owner exists
        LocalUser owner = localUserRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "USER_NOT_FOUND"));

        // Create organization
        Organization organization = organizationMapper.toEntity(createOrganizationDto);
        organization.setOwnerId(ownerId);
        organization = organizationRepository.save(organization);

        // Add owner as a member
        // TODO: Add default role for new organization
        Membership membership = new Membership(organization, owner);
        membershipRepository.save(membership);

        return organization;
    }

    public Organization getOrganizationById(UUID id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));
    }

    @Transactional
    public Organization updateOrganization(UUID id, UpdateOrganizationDto updateOrganizationDto) {
        Organization organization = this.getOrganizationById(id);

        organizationMapper.updateEntity(organization, updateOrganizationDto);
        return organizationRepository.save(organization);
    }

    @Transactional
    public boolean deleteOrganization(UUID id) {
        organizationRepository.deleteById(id);
        return true;
    }

    public boolean isOwner(UUID id, UUID ownerId) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        return organization.getOwnerId().equals(ownerId);
    }
}