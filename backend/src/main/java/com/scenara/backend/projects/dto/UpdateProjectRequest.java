package com.scenara.backend.projects.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.scenara.backend.projects.model.ProjectStatus;

public record UpdateProjectRequest(
        @Size(max = 120) @Pattern(regexp = ".*\\S.*", message = "must not be blank") String name,
        @Size(max = 1000) String description,
        ProjectStatus status
) {
}
