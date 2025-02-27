package com.taskify.organization.service;

import com.taskify.organization.dto.user.CreateLocalUserDto;
import com.taskify.organization.entity.LocalUser;
import com.taskify.organization.mapper.LocalUserMapper;
import com.taskify.organization.repository.LocalUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}