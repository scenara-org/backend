package com.scenara.backend.simulations.dto;

import java.math.BigDecimal;

public record SimulationResultsDto(
        BigDecimal totalRevenue,
        BigDecimal totalCosts,
        BigDecimal ebitda,
        BigDecimal ebitdaMargin,
        BigDecimal ebit,
        BigDecimal totalIncomeTax,
        BigDecimal netIncome,
        BigDecimal totalCapex,
        BigDecimal totalFreeCashFlow,
        BigDecimal initialInvestment,
        BigDecimal finalAccumulatedCashFlow,
        Integer paybackMonth
) {
}
