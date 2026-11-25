package com.scenara.backend.simulations.calculator;

import com.scenara.backend.scenarios.model.FinancialAssumptions;
import com.scenara.backend.simulations.model.MonthlyProjection;
import com.scenara.backend.simulations.model.SimulationResults;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FinancialProjectionCalculatorTest {

    private final FinancialProjectionCalculator calculator = new FinancialProjectionCalculator();

    /** Cenário mínimo: só receita de produto, custo fixo e investimento; sem impostos, capital de giro ou ativos. */
    private FinancialAssumptions simpleAssumptions() {
        FinancialAssumptions a = new FinancialAssumptions();
        a.setInitialProductRevenue(new BigDecimal("1000"));
        a.setInitialServiceRevenue(BigDecimal.ZERO);
        a.setInitialOtherRevenue(BigDecimal.ZERO);
        a.setProductRevenueGrowth(new BigDecimal("0.10"));
        a.setServiceRevenueGrowth(BigDecimal.ZERO);
        a.setOtherRevenueGrowth(BigDecimal.ZERO);
        a.setVariableCostRate(BigDecimal.ZERO);
        a.setInitialFixedCost(new BigDecimal("400"));
        a.setFixedCostGrowthRate(BigDecimal.ZERO);
        a.setInitialInvestment(new BigDecimal("1500"));
        a.setDepreciableAssetValue(BigDecimal.ZERO);
        a.setUsefulLifeMonths(12);
        a.setAmortizationPerMonth(BigDecimal.ZERO);
        a.setCapexRate(BigDecimal.ZERO);
        a.setInterestExpense(BigDecimal.ZERO);
        a.setEffectiveTaxRate(BigDecimal.ZERO);
        a.setDaysSalesOutstanding(0);
        a.setDaysPayableOutstanding(0);
        a.setDaysInventoryOutstanding(0);
        a.setProjectionMonths(3);
        return a;
    }

    @Test
    void firstMonthUsesInitialValuesAndGrowthCompoundsAfterwards() {
        List<MonthlyProjection> months = calculator.calculate(simpleAssumptions()).monthlyProjections();

        assertEquals(3, months.size());
        assertEquals(0, new BigDecimal("1000").compareTo(months.get(0).getTotalRevenue()));
        assertEquals(0, new BigDecimal("1100").compareTo(months.get(1).getTotalRevenue()));
        assertEquals(0, new BigDecimal("1210").compareTo(months.get(2).getTotalRevenue()));
    }

    @Test
    void consolidatesTotalsAndFindsPaybackMonth() {
        SimulationResults results = calculator.calculate(simpleAssumptions()).results();

        // FCF mensal: 600, 700, 810 -> acumulado: -900, -200, +610
        assertEquals(0, new BigDecimal("3310").compareTo(results.getTotalRevenue()));
        assertEquals(0, new BigDecimal("1200").compareTo(results.getTotalCosts()));
        assertEquals(0, new BigDecimal("2110").compareTo(results.getTotalFreeCashFlow()));
        assertEquals(0, new BigDecimal("1500").compareTo(results.getInitialInvestment()));
        assertEquals(0, new BigDecimal("610").compareTo(results.getFinalAccumulatedCashFlow()));
        assertEquals(3, results.getPaybackMonth());
    }

    @Test
    void paybackIsNullWhenInvestmentIsNotRecoveredWithinHorizon() {
        FinancialAssumptions a = simpleAssumptions();
        a.setProjectionMonths(2);

        assertNull(calculator.calculate(a).results().getPaybackMonth());
    }

    @Test
    void appliesTaxOnlyOnPositiveTaxableIncomeAndComputesNetIncome() {
        FinancialAssumptions a = simpleAssumptions();
        a.setProjectionMonths(1);
        a.setEffectiveTaxRate(new BigDecimal("0.25"));
        a.setInterestExpense(new BigDecimal("100"));

        MonthlyProjection month = calculator.calculate(a).monthlyProjections().getFirst();

        // EBITDA 600, juros 100 -> lucro tributável 500, IR 125, lucro líquido 375
        assertEquals(0, new BigDecimal("500").compareTo(month.getTaxableIncome()));
        assertEquals(0, new BigDecimal("125").compareTo(month.getIncomeTax()));
        assertEquals(0, new BigDecimal("375").compareTo(month.getNetIncome()));
    }

    @Test
    void inventoryUsesDaysInventoryOutstanding() {
        FinancialAssumptions a = simpleAssumptions();
        a.setProjectionMonths(1);
        a.setVariableCostRate(new BigDecimal("0.30"));
        a.setDaysInventoryOutstanding(15);

        MonthlyProjection month = calculator.calculate(a).monthlyProjections().getFirst();

        // Custo variável 300 * 15/30 dias = 150
        assertEquals(0, new BigDecimal("150").compareTo(month.getInventory()));
    }

    /**
     * Regressão: com crescimento composto em horizonte longo os valores acumulavam casas decimais
     * e o Mongo recusava salvar ("Conversion to Decimal128 would require inexact rounding").
     */
    @Test
    void longCompoundGrowthStaysWithinDecimal128AndInCents() {
        FinancialAssumptions a = simpleAssumptions();
        a.setProductRevenueGrowth(new BigDecimal("0.005"));
        a.setServiceRevenueGrowth(new BigDecimal("0.0123456789123"));
        a.setInitialServiceRevenue(new BigDecimal("333.333"));
        a.setFixedCostGrowthRate(new BigDecimal("0.0037"));
        a.setVariableCostRate(new BigDecimal("0.333"));
        a.setEffectiveTaxRate(new BigDecimal("0.34"));
        a.setDepreciableAssetValue(new BigDecimal("30000"));
        a.setUsefulLifeMonths(7);
        a.setDaysSalesOutstanding(31);
        a.setDaysPayableOutstanding(17);
        a.setDaysInventoryOutstanding(13);
        a.setCapexRate(new BigDecimal("0.0275"));
        a.setProjectionMonths(600);

        FinancialProjectionResult result = calculator.calculate(a);

        for (MonthlyProjection m : result.monthlyProjections()) {
            for (BigDecimal value : List.of(m.getProductRevenue(), m.getServiceRevenue(), m.getTotalRevenue(),
                    m.getVariableCost(), m.getFixedCost(), m.getEbitda(), m.getDepreciation(), m.getIncomeTax(),
                    m.getNetIncome(), m.getAccountsReceivable(), m.getInventory(), m.getAccountsPayable(),
                    m.getWorkingCapital(), m.getCapex(), m.getFreeCashFlow(), m.getAccumulatedCashFlow())) {
                assertTrue(value.scale() <= 2, "valor monetário fora de centavos: " + value);
                assertTrue(value.precision() <= 34, "valor não cabe em Decimal128: " + value);
            }
            assertTrue(m.getEbitdaMargin().precision() <= 34);
            // As linhas fecham: receita total = soma das receitas exibidas.
            assertEquals(0, m.getTotalRevenue().compareTo(
                    m.getProductRevenue().add(m.getServiceRevenue()).add(m.getOtherRevenue())));
        }
        assertTrue(result.results().getTotalRevenue().precision() <= 34);
        // Crescimento composto preservado: 1000 * 1.005^599, arredondado em centavos.
        assertEquals(0, new BigDecimal("19836.77").compareTo(result.monthlyProjections().getLast().getProductRevenue()));
    }
}
