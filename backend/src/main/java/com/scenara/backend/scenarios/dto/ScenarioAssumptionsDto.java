package com.scenara.backend.scenarios.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ScenarioAssumptionsDto(
        // Receita
        @NotNull @DecimalMin("0.00") BigDecimal initialProductRevenue,
        @NotNull @DecimalMin("0.00") BigDecimal initialServiceRevenue,
        @NotNull @DecimalMin("0.00") BigDecimal initialOtherRevenue,
        @NotNull @DecimalMin("-1.00") BigDecimal productRevenueGrowth,
        @NotNull @DecimalMin("-1.00") BigDecimal serviceRevenueGrowth,
        @NotNull @DecimalMin("-1.00") BigDecimal otherRevenueGrowth,

        // Custos
        @NotNull @DecimalMin("0.00") BigDecimal variableCostRate,
        @NotNull @DecimalMin("0.00") BigDecimal initialFixedCost,
        @NotNull @DecimalMin("-1.00") BigDecimal fixedCostGrowthRate,

        // Investimento e ativos
        @NotNull @DecimalMin("0.00") BigDecimal initialInvestment,
        @NotNull @DecimalMin("0.00") BigDecimal depreciableAssetValue,
        @NotNull @Positive Integer usefulLifeMonths,
        @NotNull @DecimalMin("0.00") BigDecimal amortizationPerMonth,
        @NotNull @DecimalMin("0.00") BigDecimal capexRate,

        // Financiamento
        @NotNull @DecimalMin("0.00") BigDecimal interestExpense,

        // Impostos
        @NotNull @DecimalMin("0.00") @DecimalMax("1.00") BigDecimal effectiveTaxRate,

        // Capital de giro
        @NotNull @PositiveOrZero Integer daysSalesOutstanding,
        @NotNull @PositiveOrZero Integer daysPayableOutstanding,
        @NotNull @PositiveOrZero Integer daysInventoryOutstanding,

        // Horizonte
        @NotNull @Positive @Max(600) Integer projectionMonths
) {
}
