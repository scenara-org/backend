package com.scenara.backend.simulations.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class SimulationResults {
    private BigDecimal totalRevenue;
    private BigDecimal totalCosts;
    private BigDecimal ebitda;
    private BigDecimal ebitdaMargin;
    private BigDecimal ebit;
    private BigDecimal totalIncomeTax;
    private BigDecimal netIncome;
    private BigDecimal totalCapex;
    private BigDecimal totalFreeCashFlow;
    private BigDecimal initialInvestment;
    private BigDecimal finalAccumulatedCashFlow;
    /** Primeiro mês em que o fluxo acumulado fica >= 0; null se não houver payback no horizonte. */
    private Integer paybackMonth;
}
