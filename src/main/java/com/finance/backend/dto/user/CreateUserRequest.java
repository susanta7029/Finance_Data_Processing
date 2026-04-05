package com.finance.backend.dto.user;

import com.finance.backend.model.Role;
import com.finance.backend.model.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 190) String email,
        @NotNull Role role,
        @NotNull UserStatus status
) {
}
