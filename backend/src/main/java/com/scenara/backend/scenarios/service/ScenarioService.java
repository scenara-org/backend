package com.scenara.backend.scenarios.service;

import com.scenara.backend.scenarios.dto.CreateScenarioRequest;
import com.scenara.backend.scenarios.dto.ScenarioResponse;
import com.scenara.backend.scenarios.dto.UpdateScenarioRequest;
import com.scenara.backend.scenarios.mapper.ScenarioMapper;
import com.scenara.backend.projects.service.ProjectAccessService;
import com.scenara.backend.scenarios.model.ScenarioModel;
import com.scenara.backend.scenarios.repository.ScenarioRepository;
import com.scenara.backend.simulations.repository.SimulationRepository;
import com.scenara.backend.simulations.service.SimulationQueryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class ScenarioService {

    private final ScenarioRepository repository;
    private final ScenarioMapper scenarioMapper;
    private final ProjectAccessService projectAccessService;
    private final SimulationRepository simulationRepository;
    private final SimulationQueryService simulationQueryService;

    public ScenarioResponse criar(String projectId, String userId, CreateScenarioRequest request) {
        projectAccessService.requireOwner(projectId, userId);

        ScenarioModel scenarioModel = new ScenarioModel();
        Instant now = Instant.now();

        scenarioModel.setProjectId(projectId);
        scenarioModel.setName(request.name());
        scenarioModel.setDescription(request.description());
        scenarioModel.setAssumptions(scenarioMapper.toModel(request.assumptions()));
        scenarioModel.setVersion(1);
        scenarioModel.setCreatedAt(now);
        scenarioModel.setUpdatedAt(now);

        return scenarioMapper.toResponse(repository.save(scenarioModel));
    }

    public ScenarioResponse exibir(String id, String projectId, String userId) {
        projectAccessService.requireOwner(projectId, userId);
        return scenarioMapper.toResponse(buscar(id, projectId));
    }

    public List<ScenarioResponse> exibirTodos(String projectId, String userId) {
        projectAccessService.requireOwner(projectId, userId);
        return repository.findAllByProjectId(projectId).stream()
                .map(scenarioMapper::toResponse)
                .toList();
    }

    public ScenarioResponse atualizar(String id, String projectId, String userId, UpdateScenarioRequest request) {
        projectAccessService.requireOwner(projectId, userId);
        ScenarioModel scenario = buscar(id, projectId);

        // Premissas diferentes geram uma nova versão; simulações antigas guardam a versão que usaram.
        boolean assumptionsChanged = request.assumptions() != null
                && !request.assumptions().equals(scenarioMapper.toDto(scenario.getAssumptions()));

        scenarioMapper.update(request, scenario);
        if (assumptionsChanged) {
            scenario.setVersion(scenario.getVersion() + 1);
        }
        scenario.setUpdatedAt(Instant.now());

        return scenarioMapper.toResponse(repository.save(scenario));
    }

    public void deletar(String id, String projectId, String userId) {
        projectAccessService.requireOwner(projectId, userId);
        if (!repository.existsByIdAndProjectId(id, projectId)) {
            throw naoEncontrado();
        }

        simulationRepository.deleteAllByProjectIdAndScenarioId(projectId, id);
        repository.deleteByIdAndProjectId(id, projectId);
        simulationQueryService.invalidarCenario(projectId, id);
    }

    public ScenarioModel buscar(String id, String projectId) {
        return repository.findByIdAndProjectId(id, projectId)
                .orElseThrow(this::naoEncontrado);
    }

    public ResponseStatusException naoEncontrado() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Cenário não encontrado");
    }
}

