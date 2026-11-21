package com.scenara.backend.scenarios.dto;

import java.time.Instant;
import java.util.List;

public record ScenarioResponse(
        String id,
        String projectId,
        String name,
        String description,
        Integer version,
        ScenarioAssumptionsDto assumptions,
        Instant createdAt,
        Instant updatedAt
) {
}
