package com.scenara.backend.projects.service;

import com.scenara.backend.projects.model.ProjectModel;
import com.scenara.backend.projects.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

/**
 * Verifica se o projeto pertence ao usuário. Roda em toda requisição de cenários e simulações,
 * então o dono de cada projeto fica em cache no Redis ("project-owner:{projectId}" -> userId).
 * O dono de um projeto nunca muda; a chave só é removida quando o projeto é excluído.
 */
@Service
@RequiredArgsConstructor
public class ProjectAccessService {

    private static final String OWNER_PREFIX = "project-owner:";
    private static final Duration OWNER_TTL = Duration.ofMinutes(30);

    private final ProjectRepository projectRepository;
    private final StringRedisTemplate redis;

    public void requireOwner(String projectId, String userId) {
        String key = OWNER_PREFIX + projectId;
        String owner = redis.opsForValue().get(key);

        if (owner == null) {
            owner = projectRepository.findById(projectId).map(ProjectModel::getUserId).orElse(null);
            if (owner != null) {
                redis.opsForValue().set(key, owner, OWNER_TTL);
            }
        }

        if (owner == null || !owner.equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Projeto não encontrado."
            );
        }
    }

    public void invalidar(String projectId) {
        redis.delete(OWNER_PREFIX + projectId);
    }
}
