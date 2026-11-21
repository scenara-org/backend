package com.scenara.backend.scenarios.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@NoArgsConstructor
@Getter
@Setter
public class FinancialAssumptions {

    private BigDecimal initialInvestment;
    private BigDecimal initialRevenue;
    private BigDecimal initialCost;
    private BigDecimal revenueGrowth;
    private BigDecimal costGrowth;
    private Integer projectionMonths;
}
