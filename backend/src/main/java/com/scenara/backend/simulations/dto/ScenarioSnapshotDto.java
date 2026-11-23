package com.scenara.backend.simulations.dto;

import com.scenara.backend.scenarios.dto.ScenarioAssumptionsDto;

public record ScenarioSnapshotDto(
        String name,
        Integer version,
        ScenarioAssumptionsDto assumptions
) {
}
