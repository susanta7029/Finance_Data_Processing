package com.finance.backend.dto.user;

import com.finance.backend.model.Role;
import com.finance.backend.model.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(max = 120) String name,
        @Email @Size(max = 190) String email,
        Role role,
        UserStatus status
) {
}
