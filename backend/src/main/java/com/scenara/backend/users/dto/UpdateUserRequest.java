package com.scenara.backend.users.dto;

public record UpdateUserRequest(
        String name,
        String email
) {
}
