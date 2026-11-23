package com.scenara.backend.simulations.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "simulations")
@Getter
@Setter
@NoArgsConstructor
public class SimulationModel {

    @Id
    private String id;

    private String userId;
    private String projectId;
    private String scenarioId;

    private ScenarioSnapshot scenarioSnapshot;
    private SimulationResults results;
    private List<MonthlyProjection> monthlyProjections;
    private Instant executedAt;
}
