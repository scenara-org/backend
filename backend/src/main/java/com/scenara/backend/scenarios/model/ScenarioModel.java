package com.scenara.backend.scenarios.model;

import com.scenara.backend.scenarios.dto.ScenarioAssumptionsDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "scenarios")
@NoArgsConstructor
@Getter
@Setter
public class ScenarioModel {

    @Id
    private String id;

    public String projectId;

    private String name;
    private String description;
    private Integer version;


    private FinancialAssumptions assumptions;

    private Instant createdAt;
    private Instant updatedAt;

}
