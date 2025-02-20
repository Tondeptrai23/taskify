package com.taskify.organization.service;

import com.taskify.organization.dto.user.CreateLocalUserDto;
import com.taskify.organization.dto.user.LocalUserCollectionRequest;
import com.taskify.organization.dto.user.UpdateLocalUserDto;
import com.taskify.organization.entity.LocalUser;
import com.taskify.organization.exception.ResourceNotFoundException;
import com.taskify.organization.exception.UserAlreadyExistsException;
import com.taskify.organization.mapper.LocalUserMapper;
import com.taskify.organization.repository.LocalUserRepository;
import com.taskify.organization.specification.LocalUserSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LocalUserService {
    private final LocalUserRepository localUserRepository;
    private final LocalUserMapper localUserMapper;

    @Autowired
    public LocalUserService(LocalUserRepository localUserRepository, LocalUserMapper localUserMapper) {
        this.localUserRepository = localUserRepository;
        this.localUserMapper = localUserMapper;
    }

    public List<LocalUser> getUsersByIds(List<UUID> ids) {
        return localUserRepository.findAllById(ids);
    }

    @Transactional
    public LocalUser createUser(CreateLocalUserDto createLocalUserDto) {
        // Check if user already exists
        if (localUserRepository.existsByEmail(createLocalUserDto.getEmail())) {
            return null;
        }

        LocalUser localUser = localUserMapper.toEntity(createLocalUserDto);
        return localUserRepository.save(localUser);
    }

    @Transactional
    public LocalUser createOrUpdateLocalUser(CreateLocalUserDto createLocalUserDto) {
        // Check if user exists by ID
        Optional<LocalUser> existingUser = localUserRepository.findById(createLocalUserDto.getId());

        if (existingUser.isPresent()) {
            // Update existing user
            LocalUser user = existingUser.get();
            user.setUsername(createLocalUserDto.getUsername());
            // Email should not be updated if user already exists
            return localUserRepository.save(user);
        } else {
            // Create new user
            return createUser(createLocalUserDto);
        }
    }

    public LocalUser getUserById(UUID id) {
        return localUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "USER_NOT_FOUND"));
    }
}