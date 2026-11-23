package com.scenara.backend.scenarios.controller;

import com.scenara.backend.scenarios.dto.CreateScenarioRequest;
import com.scenara.backend.scenarios.dto.ScenarioResponse;
import com.scenara.backend.scenarios.dto.UpdateScenarioRequest;
import com.scenara.backend.scenarios.service.ScenarioService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/scenarios")
@AllArgsConstructor
public class ScenarioController {

    private final ScenarioService service;

    @PostMapping
    public ResponseEntity<ScenarioResponse> criar(
            @PathVariable String projectId,
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody CreateScenarioRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.criar(projectId, userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScenarioResponse> exibir(
            @PathVariable String projectId,
            @PathVariable String id,
            @AuthenticationPrincipal String userId
    ) {
        return ResponseEntity.ok(service.exibir(id, projectId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ScenarioResponse>> exibirTodos(
            @PathVariable String projectId,
            @AuthenticationPrincipal String userId
    ) {
        return ResponseEntity.ok(service.exibirTodos(projectId, userId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ScenarioResponse> atualizar(
            @PathVariable String projectId,
            @PathVariable String id,
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody UpdateScenarioRequest request
    ) {
        return ResponseEntity.ok(service.atualizar(id, projectId, userId, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(
            @PathVariable String projectId,
            @PathVariable String id,
            @AuthenticationPrincipal String userId
    ) {
        service.deletar(id, projectId, userId);
    }
}
