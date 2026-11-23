package com.scenara.backend.simulations.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class MonthlyProjection {
    private Integer month;
    private BigDecimal productRevenue;
    private BigDecimal serviceRevenue;
    private BigDecimal otherRevenue;
    private BigDecimal totalRevenue;
    private BigDecimal variableCost;
    private BigDecimal fixedCost;
    private BigDecimal operatingCosts;
    private BigDecimal ebitda;
    private BigDecimal ebitdaMargin;
    private BigDecimal depreciation;
    private BigDecimal amortization;
    private BigDecimal ebit;
    private BigDecimal interestExpense;
    private BigDecimal taxableIncome;
    private BigDecimal incomeTax;
    private BigDecimal netIncome;
    private BigDecimal accountsReceivable;
    private BigDecimal inventory;
    private BigDecimal accountsPayable;
    private BigDecimal workingCapital;
    private BigDecimal changeInWorkingCapital;
    private BigDecimal capex;
    private BigDecimal freeCashFlow;
    private BigDecimal accumulatedCashFlow;
}
