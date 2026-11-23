package com.scenara.backend.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 254) String email,
        // BCrypt considera no máximo 72 bytes da senha.
        @NotBlank @Size(min = 6, max = 72) String password
) {
}
