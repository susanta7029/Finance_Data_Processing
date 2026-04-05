package com.finance.backend.service;

import com.finance.backend.dto.user.CreateUserRequest;
import com.finance.backend.dto.user.UpdateUserRequest;
import com.finance.backend.dto.user.UserResponse;
import com.finance.backend.exception.BadRequestException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.model.AppUser;
import com.finance.backend.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final AppUserRepository appUserRepository;

    public UserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return appUserRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return toResponse(user);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (appUserRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BadRequestException("Email is already in use");
        }

        AppUser user = new AppUser();
        user.setName(request.name().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setRole(request.role());
        user.setStatus(request.status());

        return toResponse(appUserRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name().trim());
        }

        if (request.email() != null && !request.email().isBlank()) {
            String normalizedEmail = request.email().trim().toLowerCase();
            if (!normalizedEmail.equalsIgnoreCase(user.getEmail()) && appUserRepository.existsByEmailIgnoreCase(normalizedEmail)) {
                throw new BadRequestException("Email is already in use");
            }
            user.setEmail(normalizedEmail);
        }

        if (request.role() != null) {
            user.setRole(request.role());
        }

        if (request.status() != null) {
            user.setStatus(request.status());
        }

        return toResponse(appUserRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        appUserRepository.delete(user);
    }

    public UserResponse toResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
