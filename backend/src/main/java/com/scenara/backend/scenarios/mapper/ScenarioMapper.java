package com.scenara.backend.scenarios.mapper;


import com.scenara.backend.scenarios.dto.CreateScenarioRequest;
import com.scenara.backend.scenarios.dto.ScenarioAssumptionsDto;
import com.scenara.backend.scenarios.dto.ScenarioResponse;
import com.scenara.backend.scenarios.dto.UpdateScenarioRequest;
import com.scenara.backend.scenarios.model.FinancialAssumptions;
import com.scenara.backend.scenarios.model.ScenarioModel;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ScenarioMapper {

    FinancialAssumptions toModel (ScenarioAssumptionsDto dto);

    ScenarioAssumptionsDto toDto (FinancialAssumptions model);



    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "version", ignore = true)
    void update(UpdateScenarioRequest request, @MappingTarget ScenarioModel scenarioModel);

    ScenarioResponse toResponse(ScenarioModel scenarioModel);
}
