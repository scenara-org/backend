package com.scenara.backend.simulations.service;

import com.scenara.backend.projects.service.ProjectAccessService;
import com.scenara.backend.scenarios.model.ScenarioModel;
import com.scenara.backend.scenarios.repository.ScenarioRepository;
import com.scenara.backend.simulations.calculator.FinancialProjectionCalculator;
import com.scenara.backend.simulations.calculator.FinancialProjectionResult;
import com.scenara.backend.simulations.dto.SimulationResponse;
import com.scenara.backend.simulations.dto.SimulationSummaryResponse;
import com.scenara.backend.simulations.mapper.SimulationMapper;
import com.scenara.backend.simulations.model.ScenarioSnapshot;
import com.scenara.backend.simulations.model.SimulationModel;
import com.scenara.backend.simulations.repository.SimulationRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class SimulationService {

    private final SimulationRepository simulationRepository;
    private final ScenarioRepository scenarioRepository;
    private final SimulationMapper mapper;
    private final ProjectAccessService projectAccessService;
    private final FinancialProjectionCalculator calculator;

    public SimulationResponse executar(String projectId, String scenarioId, String userId) {
        projectAccessService.requireOwner(projectId, userId);
        ScenarioModel scenario = scenarioRepository.findByIdAndProjectId(scenarioId, projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cenário não encontrado."));

        FinancialProjectionResult projection = calculator.calculate(scenario.getAssumptions());

        ScenarioSnapshot snapshot = new ScenarioSnapshot();
        snapshot.setName(scenario.getName());
        snapshot.setVersion(scenario.getVersion());
        snapshot.setAssumptions(mapper.copy(scenario.getAssumptions()));

        SimulationModel simulation = new SimulationModel();
        simulation.setUserId(userId);
        simulation.setProjectId(projectId);
        simulation.setScenarioId(scenarioId);
        simulation.setScenarioSnapshot(snapshot);
        simulation.setResults(projection.results());
        simulation.setMonthlyProjections(projection.monthlyProjections());
        simulation.setExecutedAt(Instant.now());

        return mapper.toResponse(simulationRepository.save(simulation));
    }

    public SimulationResponse exibir(String projectId, String id, String userId) {
        projectAccessService.requireOwner(projectId, userId);
        return simulationRepository.findByIdAndProjectId(id, projectId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Simulação não encontrada."));
    }

    public List<SimulationSummaryResponse> listarPorCenario(String projectId, String scenarioId, String userId) {
        projectAccessService.requireOwner(projectId, userId);
        if (!scenarioRepository.existsByIdAndProjectId(scenarioId, projectId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cenário não encontrado.");
        }
        return simulationRepository.findSummariesByProjectIdAndScenarioId(projectId, scenarioId)
                .stream()
                .map(mapper::toSummary)
                .toList();
    }

    public List<SimulationSummaryResponse> listarPorProjeto(String projectId, String userId) {
        projectAccessService.requireOwner(projectId, userId);
        return simulationRepository.findSummariesByProjectId(projectId)
                .stream()
                .map(mapper::toSummary)
                .toList();
    }
}
