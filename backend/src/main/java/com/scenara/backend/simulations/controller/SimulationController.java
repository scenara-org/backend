package com.scenara.backend.simulations.controller;

import com.scenara.backend.simulations.dto.SimulationResponse;
import com.scenara.backend.simulations.dto.SimulationSummaryResponse;
import com.scenara.backend.simulations.service.SimulationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}")
@AllArgsConstructor
public class SimulationController {

    private final SimulationService service;

    /** Executa uma nova simulação com as premissas atuais do cenário. */
    @PostMapping("/scenarios/{scenarioId}/simulations")
    public ResponseEntity<SimulationResponse> executar(
            @PathVariable String projectId,
            @PathVariable String scenarioId,
            @AuthenticationPrincipal String userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.executar(projectId, scenarioId, userId));
    }

    /** Histórico do cenário, da mais recente para a mais antiga, sem a projeção mensal. */
    @GetMapping("/scenarios/{scenarioId}/simulations")
    public ResponseEntity<List<SimulationSummaryResponse>> listarPorCenario(
            @PathVariable String projectId,
            @PathVariable String scenarioId,
            @AuthenticationPrincipal String userId
    ) {
        return ResponseEntity.ok(service.listarPorCenario(projectId, scenarioId, userId));
    }

    /** Todas as simulações do projeto, da mais recente para a mais antiga, sem a projeção mensal. */
    @GetMapping("/simulations")
    public ResponseEntity<List<SimulationSummaryResponse>> listarPorProjeto(
            @PathVariable String projectId,
            @AuthenticationPrincipal String userId
    ) {
        return ResponseEntity.ok(service.listarPorProjeto(projectId, userId));
    }

    /** Simulação completa, com a projeção mês a mês. */
    @GetMapping("/simulations/{id}")
    public ResponseEntity<SimulationResponse> exibir(
            @PathVariable String projectId,
            @PathVariable String id,
            @AuthenticationPrincipal String userId
    ) {
        return ResponseEntity.ok(service.exibir(projectId, id, userId));
    }
}
