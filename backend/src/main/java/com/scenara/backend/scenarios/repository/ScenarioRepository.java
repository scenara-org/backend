package com.scenara.backend.scenarios.repository;

import com.scenara.backend.projects.model.ProjectModel;
import com.scenara.backend.scenarios.model.ScenarioModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScenarioRepository extends MongoRepository<ScenarioModel, String> {
    Optional<ScenarioModel> findByIdAndProjectId(String id, String projectId);

    void deleteByIdAndProjectId(String id, String projectId);

    List<ScenarioModel> findAllByProjectId(String projectId);

    boolean existsByIdAndProjectId(String id, String projectId);
}
