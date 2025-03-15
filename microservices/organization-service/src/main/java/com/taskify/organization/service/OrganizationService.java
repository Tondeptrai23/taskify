package com.taskify.organization.service;

import com.taskify.commoncore.error.resource.OrganizationNotFoundException;
import com.taskify.commoncore.error.resource.UserNotFoundException;
import com.taskify.organization.dto.organization.CreateOrganizationDto;
import com.taskify.organization.dto.organization.OrganizationCollectionRequest;
import com.taskify.organization.dto.organization.UpdateOrganizationDto;
import com.taskify.organization.entity.LocalUser;
import com.taskify.organization.entity.Membership;
import com.taskify.organization.entity.Organization;
import com.taskify.organization.event.OrganizationEventPublisher;
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
    private final OrganizationRepository _organizationRepository;
    private final LocalUserRepository _localUserRepository;
    private final MembershipRepository _membershipRepository;
    private final OrganizationMapper _organizationMapper;
    private final OrganizationEventPublisher _organizationEventPublisher;

    @Autowired
    public OrganizationService(
            OrganizationRepository organizationRepository,
            LocalUserRepository localUserRepository,
            MembershipRepository membershipRepository,
            OrganizationMapper organizationMapper,
            OrganizationEventPublisher organizationEventPublisher
    ) {
        this._organizationRepository = organizationRepository;
        this._localUserRepository = localUserRepository;
        this._membershipRepository = membershipRepository;
        this._organizationMapper = organizationMapper;
        this._organizationEventPublisher = organizationEventPublisher;
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
    public Organization createOrganization(CreateOrganizationDto createOrganizationDto, UUID ownerId) {
        // Ensure owner exists
        LocalUser owner = _localUserRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Create organization
        Organization organization = _organizationMapper.toEntity(createOrganizationDto);
        organization.setOwnerId(ownerId);
        organization = _organizationRepository.save(organization);

        // Add owner as a member
        // TODO: Add default role for new organization
        Membership membership = new Membership(organization, owner);
        _membershipRepository.save(membership);

        // Publish organization created event
        _organizationEventPublisher.publishOrganizationCreatedEvent(organization);

        return organization;
    }

    public Organization getOrganizationById(UUID id) {
        return _organizationRepository.findById(id)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));
    }

    @Transactional
    public Organization updateOrganization(UUID id, UpdateOrganizationDto updateOrganizationDto) {
        Organization organization = this.getOrganizationById(id);

        _organizationMapper.updateEntity(organization, updateOrganizationDto);
        organization = _organizationRepository.save(organization);

        // Publish organization updated event
        _organizationEventPublisher.publishOrganizationUpdatedEvent(organization);

        return organization;
    }

    @Transactional
    public boolean deleteOrganization(UUID id) {
        // Get the organization before deleting it
        Organization organization = _organizationRepository.findById(id)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        _organizationRepository.deleteById(id);

        // Publish organization deleted event
        _organizationEventPublisher.publishOrganizationDeletedEvent(id);

        return true;
    }

    public boolean isOwner(UUID id, UUID ownerId) {
        Organization organization = _organizationRepository.findById(id)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        return organization.getOwnerId().equals(ownerId);
    }
}