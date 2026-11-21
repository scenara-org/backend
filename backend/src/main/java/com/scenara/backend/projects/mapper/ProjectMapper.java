package com.scenara.backend.projects.mapper;

import com.scenara.backend.projects.dto.CreateProjectRequest;
import com.scenara.backend.projects.dto.ProjectResponse;
import com.scenara.backend.projects.dto.UpdateProjectRequest;
import com.scenara.backend.projects.model.ProjectModel;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProjectMapper {



    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void update(UpdateProjectRequest request, @MappingTarget ProjectModel project);

    ProjectResponse toResponse(ProjectModel project);
}
