package com.scenara.backend.simulations.dto;

import java.time.Instant;
import java.util.List;

public record SimulationResponse(
        String id,
        String scenarioId,
        String projectId,
        String userId,
        ScenarioSnapshotDto scenarioSnapshot,
        SimulationResultsDto results,
        List<MonthlyProjectionDto> monthlyProjections,
        Instant executedAt
) {
}
