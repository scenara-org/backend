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

    public FinancialProjectionResult calculate(FinancialAssumptions assumptions) {
        List<MonthlyProjection> projections = new ArrayList<>();

        BigDecimal productRevenue = assumptions.getInitialProductRevenue();
        BigDecimal serviceRevenue = assumptions.getInitialServiceRevenue();
        BigDecimal otherRevenue = assumptions.getInitialOtherRevenue();
        BigDecimal fixedCost = assumptions.getInitialFixedCost();
        BigDecimal accumulatedCashFlow = assumptions.getInitialInvestment().negate();
        BigDecimal previousWorkingCapital = BigDecimal.ZERO;
        BigDecimal depreciation = depreciation(assumptions);
        Integer paybackMonth = null;

        for (int month = 1; month <= assumptions.getProjectionMonths(); month++) {
            // O primeiro mês usa os valores iniciais; o crescimento composto começa no mês 2.
            if (month > 1) {
                productRevenue = grow(productRevenue, assumptions.getProductRevenueGrowth());
                serviceRevenue = grow(serviceRevenue, assumptions.getServiceRevenueGrowth());
                otherRevenue = grow(otherRevenue, assumptions.getOtherRevenueGrowth());
                fixedCost = grow(fixedCost, assumptions.getFixedCostGrowthRate());
            }

            BigDecimal totalRevenue = productRevenue.add(serviceRevenue).add(otherRevenue);
            BigDecimal variableCost = totalRevenue.multiply(assumptions.getVariableCostRate());
            BigDecimal operatingCosts = variableCost.add(fixedCost);
            BigDecimal ebitda = totalRevenue.subtract(operatingCosts);
            BigDecimal amortization = assumptions.getAmortizationPerMonth();
            BigDecimal ebit = ebitda.subtract(depreciation).subtract(amortization);
            BigDecimal interestExpense = assumptions.getInterestExpense();
            BigDecimal taxableIncome = ebit.subtract(interestExpense);
            BigDecimal incomeTax = taxableIncome.max(BigDecimal.ZERO)
                    .multiply(assumptions.getEffectiveTaxRate());
            BigDecimal netIncome = taxableIncome.subtract(incomeTax);

            BigDecimal accountsReceivable = totalRevenue.multiply(daysRatio(assumptions.getDaysSalesOutstanding()));
            BigDecimal accountsPayable = operatingCosts.multiply(daysRatio(assumptions.getDaysPayableOutstanding()));
            BigDecimal inventory = variableCost.multiply(daysRatio(assumptions.getDaysInventoryOutstanding()));
            BigDecimal workingCapital = accountsReceivable.add(inventory).subtract(accountsPayable);
            BigDecimal changeInWorkingCapital = workingCapital.subtract(previousWorkingCapital);
            BigDecimal capex = totalRevenue.multiply(assumptions.getCapexRate());
            // Fluxo de caixa livre da firma (antes do serviço da dívida).
            BigDecimal freeCashFlow = ebitda
                    .subtract(incomeTax)
                    .subtract(changeInWorkingCapital)
                    .subtract(capex);
            accumulatedCashFlow = accumulatedCashFlow.add(freeCashFlow);

            MonthlyProjection projection = new MonthlyProjection();
            projection.setMonth(month);
            projection.setProductRevenue(productRevenue);
            projection.setServiceRevenue(serviceRevenue);
            projection.setOtherRevenue(otherRevenue);
            projection.setTotalRevenue(totalRevenue);
            projection.setVariableCost(variableCost);
            projection.setFixedCost(fixedCost);
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
        return value.multiply(BigDecimal.ONE.add(growthRate));
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
        results.setInitialInvestment(assumptions.getInitialInvestment());
        results.setFinalAccumulatedCashFlow(projections.getLast().getAccumulatedCashFlow());
        results.setPaybackMonth(paybackMonth);
        return results;
    }

    private BigDecimal sum(List<MonthlyProjection> projections,
                           java.util.function.Function<MonthlyProjection, BigDecimal> value) {
        return projections.stream().map(value).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
