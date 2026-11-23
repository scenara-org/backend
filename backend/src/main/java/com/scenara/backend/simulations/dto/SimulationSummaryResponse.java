package com.scenara.backend.simulations.dto;

import java.time.Instant;

/** Versão resumida para listagens: sem a projeção mensal completa. */
public record SimulationSummaryResponse(
        String id,
        String scenarioId,
        String projectId,
        String scenarioName,
        Integer scenarioVersion,
        SimulationResultsDto results,
        Instant executedAt
) {
}
