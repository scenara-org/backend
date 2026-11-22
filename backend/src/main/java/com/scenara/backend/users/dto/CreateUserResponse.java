package com.scenara.backend.users.dto;

import java.time.Instant;
import com.scenara.backend.users.model.UserRole;

public record CreateUserResponse (
        String id,
        String name,
        String email,
        UserRole role,
        Instant createdAt,
        Instant updatedAt
){
}
