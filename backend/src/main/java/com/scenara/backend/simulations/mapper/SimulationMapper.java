package com.scenara.backend.simulations.mapper;

import com.scenara.backend.scenarios.dto.ScenarioAssumptionsDto;
import com.scenara.backend.scenarios.model.FinancialAssumptions;
import com.scenara.backend.simulations.dto.MonthlyProjectionDto;
import com.scenara.backend.simulations.dto.ScenarioSnapshotDto;
import com.scenara.backend.simulations.dto.SimulationResponse;
import com.scenara.backend.simulations.dto.SimulationResultsDto;
import com.scenara.backend.simulations.dto.SimulationSummaryResponse;
import com.scenara.backend.simulations.model.MonthlyProjection;
import com.scenara.backend.simulations.model.ScenarioSnapshot;
import com.scenara.backend.simulations.model.SimulationModel;
import com.scenara.backend.simulations.model.SimulationResults;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SimulationMapper {

    SimulationResponse toResponse(SimulationModel simulation);

    @Mapping(target = "scenarioName", source = "scenarioSnapshot.name")
    @Mapping(target = "scenarioVersion", source = "scenarioSnapshot.version")
    SimulationSummaryResponse toSummary(SimulationModel simulation);

    ScenarioSnapshotDto toDto(ScenarioSnapshot snapshot);

    SimulationResultsDto toDto(SimulationResults results);

    MonthlyProjectionDto toDto(MonthlyProjection projection);

    ScenarioAssumptionsDto toDto(FinancialAssumptions assumptions);

    /** Cópia independente das premissas, para o snapshot não mudar se o cenário for editado depois. */
    FinancialAssumptions copy(FinancialAssumptions assumptions);
}
