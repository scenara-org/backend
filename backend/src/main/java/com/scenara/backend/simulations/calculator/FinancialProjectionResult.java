package com.scenara.backend.simulations.calculator;

import com.scenara.backend.simulations.model.MonthlyProjection;
import com.scenara.backend.simulations.model.SimulationResults;

import java.util.List;

public record FinancialProjectionResult(
        List<MonthlyProjection> monthlyProjections,
        SimulationResults results
) {
}
