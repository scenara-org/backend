package com.scenara.backend.simulations.service;

import com.scenara.backend.shared.cache.CacheNames;
import com.scenara.backend.simulations.dto.SimulationResponse;
import com.scenara.backend.simulations.dto.SimulationSummaryResponse;
import com.scenara.backend.simulations.mapper.SimulationMapper;
import com.scenara.backend.simulations.repository.SimulationRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Leituras de simulações com cache no Redis.
 *
 * IMPORTANTE: não faz checagem de acesso. Quem chama precisa validar o dono do projeto antes
 * (ProjectAccessService.requireOwner); por isso o cache fica aqui e não nos métodos do SimulationService,
 * senão um acerto de cache pularia a verificação.
 */
@Service
@AllArgsConstructor
public class SimulationQueryService {

    private final SimulationRepository repository;
    private final SimulationMapper mapper;

    @Cacheable(cacheNames = CacheNames.SIMULATION, key = "#projectId + ':' + #id")
    public SimulationResponse buscar(String projectId, String id) {
        return repository.findByIdAndProjectId(id, projectId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Simulação não encontrada."));
    }

    @Cacheable(cacheNames = CacheNames.SIMULATIONS_BY_PROJECT, key = "#projectId")
    public List<SimulationSummaryResponse> resumosPorProjeto(String projectId) {
        return repository.findSummariesByProjectId(projectId).stream()
                .map(mapper::toSummary)
                .toList();
    }

    @Cacheable(cacheNames = CacheNames.SIMULATIONS_BY_SCENARIO, key = "#projectId + ':' + #scenarioId")
    public List<SimulationSummaryResponse> resumosPorCenario(String projectId, String scenarioId) {
        return repository.findSummariesByProjectIdAndScenarioId(projectId, scenarioId).stream()
                .map(mapper::toSummary)
                .toList();
    }

    /** Nova simulação executada: as listagens do projeto e do cenário mudaram. */
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.SIMULATIONS_BY_PROJECT, key = "#projectId"),
            @CacheEvict(cacheNames = CacheNames.SIMULATIONS_BY_SCENARIO, key = "#projectId + ':' + #scenarioId")
    })
    public void invalidarListagens(String projectId, String scenarioId) {
    }

    /**
     * Cenário excluído junto com suas simulações. Os detalhes são limpos por inteiro porque não sabemos
     * os IDs das simulações removidas; é uma operação rara, então o custo é aceitável.
     */
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.SIMULATIONS_BY_PROJECT, key = "#projectId"),
            @CacheEvict(cacheNames = CacheNames.SIMULATIONS_BY_SCENARIO, key = "#projectId + ':' + #scenarioId"),
            @CacheEvict(cacheNames = CacheNames.SIMULATION, allEntries = true)
    })
    public void invalidarCenario(String projectId, String scenarioId) {
    }
}
