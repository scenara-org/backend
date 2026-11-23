package com.scenara.backend.scenarios.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Premissas financeiras de um cenário. Os nomes espelham {@code ScenarioAssumptionsDto}
 * para que o MapStruct converta os dois sem mapeamentos manuais.
 * Taxas são frações (0.032 = 3,2%); prazos de capital de giro são em dias.
 */
@NoArgsConstructor
@Getter
@Setter
public class FinancialAssumptions {

    // Receita
    private BigDecimal initialProductRevenue;
    private BigDecimal initialServiceRevenue;
    private BigDecimal initialOtherRevenue;
    private BigDecimal productRevenueGrowth;
    private BigDecimal serviceRevenueGrowth;
    private BigDecimal otherRevenueGrowth;

    // Custos
    private BigDecimal variableCostRate;
    private BigDecimal initialFixedCost;
    private BigDecimal fixedCostGrowthRate;

    // Investimento e ativos
    private BigDecimal initialInvestment;
    private BigDecimal depreciableAssetValue;
    private Integer usefulLifeMonths;
    private BigDecimal amortizationPerMonth;
    private BigDecimal capexRate;

    // Financiamento (despesa de juros mensal)
    private BigDecimal interestExpense;

    // Impostos
    private BigDecimal effectiveTaxRate;

    // Capital de giro
    private Integer daysSalesOutstanding;
    private Integer daysPayableOutstanding;
    private Integer daysInventoryOutstanding;

    // Horizonte
    private Integer projectionMonths;
}
