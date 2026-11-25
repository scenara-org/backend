package com.scenara.backend.projects.service;

import com.scenara.backend.projects.dto.CreateProjectRequest;
import com.scenara.backend.projects.dto.ProjectResponse;
import com.scenara.backend.projects.dto.UpdateProjectRequest;
import com.scenara.backend.projects.mapper.ProjectMapper;
import com.scenara.backend.projects.model.ProjectModel;
import com.scenara.backend.projects.model.ProjectStatus;
import com.scenara.backend.projects.repository.ProjectRepository;
import com.scenara.backend.scenarios.repository.ScenarioRepository;
import com.scenara.backend.simulations.repository.SimulationRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class ProjectService {

    private final ProjectRepository repository;
    private final ProjectMapper mapper;
    private final ScenarioRepository scenarioRepository;
    private final SimulationRepository simulationRepository;
    private final ProjectAccessService projectAccessService;

    public ProjectResponse criar(String userId, CreateProjectRequest request) {
        ProjectModel project = new ProjectModel();
        Instant now = Instant.now();

        project.setUserId(userId);
        project.setName(request.name());
        project.setDescription(request.description());
        project.setObjective(request.objective());
        project.setStatus(ProjectStatus.ACTIVE);
        project.setCreatedAt(now);
        project.setUpdatedAt(now);

        return mapper.toResponse(repository.save(project));
    }

    public ProjectResponse exibir(String id, String userId) {
        return mapper.toResponse(buscar(id, userId));
    }

    public List<ProjectResponse>  exibirTodos(String userId) {
        return repository.findAllByUserId(userId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public ProjectResponse atualizar(String id, String userId, UpdateProjectRequest request) {
        ProjectModel project = buscar(id, userId);

        mapper.update(request, project);
        project.setUpdatedAt(Instant.now());

        return mapper.toResponse(repository.save(project));
    }


    public void deletar(String id, String userId) {
        if (!repository.existsByIdAndUserId(id, userId)) {
            throw naoEncontrado();
        }
        // Remove também o que pertence ao projeto, para não deixar cenários e simulações órfãos.
        simulationRepository.deleteAllByProjectId(id);
        scenarioRepository.deleteAllByProjectId(id);
        repository.deleteByIdAndUserId(id, userId);
        projectAccessService.invalidar(id);
    }

    private ProjectModel buscar(String id, String userId) {
        return repository.findByIdAndUserId(id, userId).orElseThrow(this::naoEncontrado);
    }

    private ResponseStatusException naoEncontrado() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado.");
    }
}
