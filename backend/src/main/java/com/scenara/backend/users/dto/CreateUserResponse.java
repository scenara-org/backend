package com.scenara.backend.users.dto;

import java.time.Instant;

public record CreateUserResponse (
        String id,
        String name,
        String email,
        Instant createdAt,
        Instant updatedAt
){
}
