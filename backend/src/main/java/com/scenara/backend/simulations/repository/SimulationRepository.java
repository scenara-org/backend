package com.scenara.backend.simulations.repository;

import com.scenara.backend.simulations.model.SimulationModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SimulationRepository extends MongoRepository<SimulationModel, String> {

    // Listagens não carregam a projeção mensal, que pode ter centenas de linhas por simulação.
    // Com @Query o Spring Data ignora o "OrderBy" do nome do método, por isso a ordenação é explícita.
    String WITHOUT_PROJECTIONS = "{ 'monthlyProjections': 0 }";
    String NEWEST_FIRST = "{ 'executedAt': -1 }";

    Optional<SimulationModel> findByIdAndProjectId(String id, String projectId);

    @Query(value = "{ 'projectId': ?0, 'scenarioId': ?1 }", fields = WITHOUT_PROJECTIONS, sort = NEWEST_FIRST)
    List<SimulationModel> findSummariesByProjectIdAndScenarioId(String projectId, String scenarioId);

    @Query(value = "{ 'projectId': ?0 }", fields = WITHOUT_PROJECTIONS, sort = NEWEST_FIRST)
    List<SimulationModel> findSummariesByProjectId(String projectId);

    void deleteAllByProjectIdAndScenarioId(String projectId, String scenarioId);

    void deleteAllByProjectId(String projectId);
}
