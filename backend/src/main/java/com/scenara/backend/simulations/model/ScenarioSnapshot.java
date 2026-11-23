package com.scenara.backend.simulations.model;

import com.scenara.backend.scenarios.model.FinancialAssumptions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScenarioSnapshot {
    private String name;
    private Integer version;
    private FinancialAssumptions assumptions;
}
