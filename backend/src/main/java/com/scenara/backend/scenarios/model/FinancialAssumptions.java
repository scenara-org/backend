package com.scenara.backend.scenarios.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;


@AllArgsConstructor
@Getter
@Setter
public class FinantialAssumptions {

    private String id;
    private BigDecimal initialInvestment;
    private BigDecimal initialRevenue;
    private BigDecimal initialCost;
    private BigDecimal reventueGrowth;
    private BigDecimal costGrowth;
    private Integer projectionMonths;

    private Instant createdAt;
    private Instant updatedAt
//    | Campo | Tipo | Descrição |
//            |---|---|---|
//            | `_id` | `ObjectId`/`String` | Identificador do cenário. |
//            | `projectId` | `ObjectId`/`String` | Referência para `projects._id`. |
//            | `name` | `String` | Nome da hipótese. |
//            | `description` | `String` | Descrição opcional da hipótese. |
//            | `version` | `Integer` | Versão das premissas do cenário. |
//            | `assumptions` | `Object` | Premissas financeiras usadas no cálculo. |
//            | `assumptions.initialInvestment` | `Decimal128` | Investimento inicial. |
//            | `assumptions.initialRevenue` | `Decimal128` | Receita mensal inicial. |
//            | `assumptions.initialCost` | `Decimal128` | Custo operacional mensal inicial. |
//            | `assumptions.revenueGrowth` | `Decimal128` | Crescimento mensal da receita (`0.08` = 8%). |
//            | `assumptions.costGrowth` | `Decimal128` | Crescimento mensal dos custos. |
//            | `assumptions.projectionMonths` | `Integer` | Quantidade de meses da projeção. |
//            | `createdAt` | `Date` | Momento de criação. |
//            | `updatedAt` | `Date`
}
