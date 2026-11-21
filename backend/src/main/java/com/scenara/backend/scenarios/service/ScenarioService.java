package com.scenara.backend.scenarios.service;

import com.scenara.backend.scenarios.dto.CreateScenarioRequest;
import com.scenara.backend.scenarios.dto.ScenarioResponse;
import com.scenara.backend.scenarios.dto.UpdateScenarioRequest;
import com.scenara.backend.scenarios.mapper.ScenarioMapper;
import com.scenara.backend.scenarios.model.FinancialAssumptions;
import com.scenara.backend.scenarios.model.ScenarioModel;
import com.scenara.backend.scenarios.repository.ScenarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ScenarioService {

    private final ScenarioRepository repository;
    private final ScenarioMapper scenarioMapper;

    public ScenarioResponse criar(String projectId, CreateScenarioRequest request) {
        ScenarioModel scenarioModel = new ScenarioModel();
        FinancialAssumptions assumptions = new FinancialAssumptions();

        scenarioModel.setProjectId(projectId);
        scenarioModel.setName(request.name());
        scenarioModel.setDescription(request.description());
        scenarioModel.setAssumptions(scenarioMapper.toModel(request.assumptions()));

        return scenarioMapper.toResponse(repository.save(scenarioModel));
    }

    public ScenarioResponse exibir(String id, String projectId) {
        return scenarioMapper.toResponse(buscar(id, projectId));
    }

    public List<ScenarioResponse> exibirTodos(String projectId) {
        return repository.findAllByProjectId(projectId).stream()
                .map(scenarioMapper::toResponse)
                .toList();
    }

    public ScenarioResponse atualizar(String id, String projectId, UpdateScenarioRequest request) {
        ScenarioModel scenario = buscar(id, projectId);

        scenarioMapper.update(request, scenario);
        scenario.setUpdatedAt(Instant.now());

        return scenarioMapper.toResponse(scenario);
    }

    public void deletar(String id, String projectId) {
        if (!repository.existsByIdAndProjectId(id, projectId)) {
            throw naoEncontrado();
        }

        repository.deleteByIdAndProjectId(id, projectId);
    }

    public ScenarioModel buscar(String id, String projectId) {
        return repository.findByIdAndProjectId(id, projectId)
                .orElseThrow(this::naoEncontrado);
    }

    public ResponseStatusException naoEncontrado() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Cenário não encontrado");
    }
}

