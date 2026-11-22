package com.scenara.backend.scenarios.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ScenarioAssumptionsDto(
        @NotNull @DecimalMin("0.00") BigDecimal initialInvestment,
        @NotNull @DecimalMin("0.00") BigDecimal initialRevenue,
        @NotNull @DecimalMin("0.00") BigDecimal initialCost,
        @NotNull @DecimalMin("-1.00") BigDecimal revenueGrowth,
        @NotNull @DecimalMin("-1.00") BigDecimal costGrowth,
        @NotNull @Positive Integer projectionMonths
) {
}
