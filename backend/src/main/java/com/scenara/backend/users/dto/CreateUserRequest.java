package com.scenara.backend.users.dto;

public record CreateUserRequest(
        String name,
        String email,
        String password
) {
}
