package com.scenara.backend.projects.service;

import com.scenara.backend.projects.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProjectAccessService {

    private final ProjectRepository projectRepository;

    public void requireOwner(String projectId, String userId) {
        if (!projectRepository.existsByIdAndUserId(projectId, userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Projeto não encontrado."
            );
        }
    }
}
