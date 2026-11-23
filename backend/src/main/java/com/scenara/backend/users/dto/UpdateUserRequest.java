package com.scenara.backend.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(max = 120) @Pattern(regexp = ".*\\S.*", message = "must not be blank") String name,
        @Email @Size(max = 254) String email
) {
}
