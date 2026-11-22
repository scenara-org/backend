package com.scenara.backend.projects.repository;

import com.scenara.backend.projects.model.ProjectModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<ProjectModel, String> {
    List<ProjectModel> findAllByUserId(String userId);

    Optional<ProjectModel> findByIdAndUserId(String id, String userId);

    void deleteByIdAndUserId(String id, String userId);

    boolean existsByIdAndUserId(String id, String userId);

}
