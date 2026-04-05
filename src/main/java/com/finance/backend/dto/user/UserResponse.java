package com.finance.backend.dto.user;

import com.finance.backend.model.Role;
import com.finance.backend.model.UserStatus;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role,
        UserStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
