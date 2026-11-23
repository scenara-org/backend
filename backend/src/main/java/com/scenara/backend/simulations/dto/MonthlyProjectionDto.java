package com.scenara.backend.simulations.dto;

import java.math.BigDecimal;

public record MonthlyProjectionDto(
        Integer month,
        BigDecimal productRevenue,
        BigDecimal serviceRevenue,
        BigDecimal otherRevenue,
        BigDecimal totalRevenue,
        BigDecimal variableCost,
        BigDecimal fixedCost,
        BigDecimal operatingCosts,
        BigDecimal ebitda,
        BigDecimal ebitdaMargin,
        BigDecimal depreciation,
        BigDecimal amortization,
        BigDecimal ebit,
        BigDecimal interestExpense,
        BigDecimal taxableIncome,
        BigDecimal incomeTax,
        BigDecimal netIncome,
        BigDecimal accountsReceivable,
        BigDecimal inventory,
        BigDecimal accountsPayable,
        BigDecimal workingCapital,
        BigDecimal changeInWorkingCapital,
        BigDecimal capex,
        BigDecimal freeCashFlow,
        BigDecimal accumulatedCashFlow
) {
}
