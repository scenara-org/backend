package com.scenara.backend.simulations.calculator;

import com.scenara.backend.scenarios.model.FinancialAssumptions;
import com.scenara.backend.simulations.model.MonthlyProjection;
import com.scenara.backend.simulations.model.SimulationResults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class FinancialProjectionCalculator {

    private static final BigDecimal DAYS_IN_MONTH = BigDecimal.valueOf(30);
    private static final int DIVISION_SCALE = 12;
    /**
     * Valores monetários saem em centavos. Sem esse arredondamento o crescimento composto acumula casas decimais
     * a cada mês (ex.: 1.005^600) e o número passa dos 34 dígitos que o Mongo aceita em Decimal128.
     */
    private static final int MONEY_SCALE = 2;
    /** Escala do estado interno que cresce mês a mês: limitada, mas fina o bastante para não distorcer o composto. */
    private static final int GROWTH_SCALE = 10;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    public FinancialProjectionResult calculate(FinancialAssumptions assumptions) {
        List<MonthlyProjection> projections = new ArrayList<>();

        BigDecimal productRevenue = assumptions.getInitialProductRevenue();
        BigDecimal serviceRevenue = assumptions.getInitialServiceRevenue();
        BigDecimal otherRevenue = assumptions.getInitialOtherRevenue();
        BigDecimal fixedCost = assumptions.getInitialFixedCost();
        BigDecimal accumulatedCashFlow = money(assumptions.getInitialInvestment()).negate();
        BigDecimal previousWorkingCapital = BigDecimal.ZERO;
        BigDecimal depreciation = money(depreciation(assumptions));
        BigDecimal amortization = money(assumptions.getAmortizationPerMonth());
        BigDecimal interestExpense = money(assumptions.getInterestExpense());
        Integer paybackMonth = null;

        for (int month = 1; month <= assumptions.getProjectionMonths(); month++) {
            // O primeiro mês usa os valores iniciais; o crescimento composto começa no mês 2.
            if (month > 1) {
                productRevenue = grow(productRevenue, assumptions.getProductRevenueGrowth());
                serviceRevenue = grow(serviceRevenue, assumptions.getServiceRevenueGrowth());
                otherRevenue = grow(otherRevenue, assumptions.getOtherRevenueGrowth());
                fixedCost = grow(fixedCost, assumptions.getFixedCostGrowthRate());
            }

            // Cada linha é arredondada em centavos antes de entrar na seguinte, como numa planilha:
            // assim os totais exibidos fecham com a soma das linhas exibidas.
            BigDecimal monthProductRevenue = money(productRevenue);
            BigDecimal monthServiceRevenue = money(serviceRevenue);
            BigDecimal monthOtherRevenue = money(otherRevenue);
            BigDecimal monthFixedCost = money(fixedCost);

            BigDecimal totalRevenue = monthProductRevenue.add(monthServiceRevenue).add(monthOtherRevenue);
            BigDecimal variableCost = money(totalRevenue.multiply(assumptions.getVariableCostRate()));
            BigDecimal operatingCosts = variableCost.add(monthFixedCost);
            BigDecimal ebitda = totalRevenue.subtract(operatingCosts);
            BigDecimal ebit = ebitda.subtract(depreciation).subtract(amortization);
            BigDecimal taxableIncome = ebit.subtract(interestExpense);
            BigDecimal incomeTax = money(taxableIncome.max(BigDecimal.ZERO)
                    .multiply(assumptions.getEffectiveTaxRate()));
            BigDecimal netIncome = taxableIncome.subtract(incomeTax);

            BigDecimal accountsReceivable = money(totalRevenue.multiply(daysRatio(assumptions.getDaysSalesOutstanding())));
            BigDecimal accountsPayable = money(operatingCosts.multiply(daysRatio(assumptions.getDaysPayableOutstanding())));
            BigDecimal inventory = money(variableCost.multiply(daysRatio(assumptions.getDaysInventoryOutstanding())));
            BigDecimal workingCapital = accountsReceivable.add(inventory).subtract(accountsPayable);
            BigDecimal changeInWorkingCapital = workingCapital.subtract(previousWorkingCapital);
            BigDecimal capex = money(totalRevenue.multiply(assumptions.getCapexRate()));
            // Fluxo de caixa livre da firma (antes do serviço da dívida).
            BigDecimal freeCashFlow = ebitda
                    .subtract(incomeTax)
                    .subtract(changeInWorkingCapital)
                    .subtract(capex);
            accumulatedCashFlow = accumulatedCashFlow.add(freeCashFlow);

            MonthlyProjection projection = new MonthlyProjection();
            projection.setMonth(month);
            projection.setProductRevenue(monthProductRevenue);
            projection.setServiceRevenue(monthServiceRevenue);
            projection.setOtherRevenue(monthOtherRevenue);
            projection.setTotalRevenue(totalRevenue);
            projection.setVariableCost(variableCost);
            projection.setFixedCost(monthFixedCost);
            projection.setOperatingCosts(operatingCosts);
            projection.setEbitda(ebitda);
            projection.setEbitdaMargin(ratio(ebitda, totalRevenue));
            projection.setDepreciation(depreciation);
            projection.setAmortization(amortization);
            projection.setEbit(ebit);
            projection.setInterestExpense(interestExpense);
            projection.setTaxableIncome(taxableIncome);
            projection.setIncomeTax(incomeTax);
            projection.setNetIncome(netIncome);
            projection.setAccountsReceivable(accountsReceivable);
            projection.setInventory(inventory);
            projection.setAccountsPayable(accountsPayable);
            projection.setWorkingCapital(workingCapital);
            projection.setChangeInWorkingCapital(changeInWorkingCapital);
            projection.setCapex(capex);
            projection.setFreeCashFlow(freeCashFlow);
            projection.setAccumulatedCashFlow(accumulatedCashFlow);
            projections.add(projection);

            if (paybackMonth == null && accumulatedCashFlow.signum() >= 0) {
                paybackMonth = month;
            }
            previousWorkingCapital = workingCapital;
        }

        return new FinancialProjectionResult(projections, consolidate(projections, assumptions, paybackMonth));
    }

    private BigDecimal grow(BigDecimal value, BigDecimal growthRate) {
        return value.multiply(BigDecimal.ONE.add(growthRate)).setScale(GROWTH_SCALE, ROUNDING);
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(MONEY_SCALE, ROUNDING);
    }

    private BigDecimal depreciation(FinancialAssumptions assumptions) {
        return assumptions.getDepreciableAssetValue().divide(
                BigDecimal.valueOf(assumptions.getUsefulLifeMonths()),
                DIVISION_SCALE,
                RoundingMode.HALF_UP
        );
    }

    private BigDecimal daysRatio(Integer days) {
        return BigDecimal.valueOf(days).divide(DAYS_IN_MONTH, DIVISION_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal ratio(BigDecimal numerator, BigDecimal denominator) {
        return denominator.signum() == 0 ? null : numerator.divide(denominator, DIVISION_SCALE, RoundingMode.HALF_UP);
    }

    private SimulationResults consolidate(List<MonthlyProjection> projections,
                                          FinancialAssumptions assumptions,
                                          Integer paybackMonth) {
        SimulationResults results = new SimulationResults();
        BigDecimal totalRevenue = sum(projections, MonthlyProjection::getTotalRevenue);
        BigDecimal ebitda = sum(projections, MonthlyProjection::getEbitda);
        results.setTotalRevenue(totalRevenue);
        results.setTotalCosts(sum(projections, MonthlyProjection::getOperatingCosts));
        results.setEbitda(ebitda);
        results.setEbitdaMargin(ratio(ebitda, totalRevenue));
        results.setEbit(sum(projections, MonthlyProjection::getEbit));
        results.setTotalIncomeTax(sum(projections, MonthlyProjection::getIncomeTax));
        results.setNetIncome(sum(projections, MonthlyProjection::getNetIncome));
        results.setTotalCapex(sum(projections, MonthlyProjection::getCapex));
        results.setTotalFreeCashFlow(sum(projections, MonthlyProjection::getFreeCashFlow));
        results.setInitialInvestment(money(assumptions.getInitialInvestment()));
        results.setFinalAccumulatedCashFlow(projections.getLast().getAccumulatedCashFlow());
        results.setPaybackMonth(paybackMonth);
        return results;
    }

    private BigDecimal sum(List<MonthlyProjection> projections,
                           java.util.function.Function<MonthlyProjection, BigDecimal> value) {
        return projections.stream().map(value).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
