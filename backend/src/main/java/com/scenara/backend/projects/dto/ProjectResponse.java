package com.scenara.backend.projects.dto;

import java.time.Instant;
import com.scenara.backend.projects.model.ProjectStatus;

public record ProjectResponse(
        String id,
        String name,
        String description,
        ProjectStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
