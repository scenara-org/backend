package com.scenara.backend.projects.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "projects")
@NoArgsConstructor
@Getter
@Setter
public class ProjectModel {

    @Id
    private String id;

    private String name;
    private String description;
    private String objective;
    private ProjectStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private String userId;

}
