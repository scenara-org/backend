package com.scenara.backend.projects.controller;

import com.scenara.backend.projects.dto.CreateProjectRequest;
import com.scenara.backend.projects.dto.ProjectResponse;
import com.scenara.backend.projects.dto.UpdateProjectRequest;
import com.scenara.backend.projects.service.ProjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("projects")
@AllArgsConstructor
public class ProjectController {

    private final ProjectService service;

    @PostMapping
    public ResponseEntity<ProjectResponse> criar(@Valid @RequestBody CreateProjectRequest request, @AuthenticationPrincipal String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> exibir(@PathVariable String id, @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(service.exibir(id, userId));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> exibirTodos(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(service.exibirTodos(userId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> atualizar(@PathVariable String id,
                                                     @AuthenticationPrincipal String userId,
                                                     @Valid @RequestBody UpdateProjectRequest request) {
        return ResponseEntity.ok(service.atualizar(id, userId, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable String id, @AuthenticationPrincipal String userId) {
        service.deletar(id, userId);
    }
}
