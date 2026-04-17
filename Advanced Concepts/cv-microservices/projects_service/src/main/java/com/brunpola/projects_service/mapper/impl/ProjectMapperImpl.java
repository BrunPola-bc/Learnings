package com.brunpola.projects_service.mapper.impl;

import com.brunpola.projects_service.domain.dto.ProjectDto;
import com.brunpola.projects_service.domain.dto.ProjectExtendedDto;
import com.brunpola.projects_service.domain.entity.ProjectEntity;
import com.brunpola.projects_service.domain.external.PersonDto;
import com.brunpola.projects_service.domain.external.SkillDto;
import com.brunpola.projects_service.mapper.ProjectMapper;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapperImpl implements ProjectMapper {

  private final ModelMapper modelMapper;

  public ProjectMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public ProjectDto toDto(ProjectEntity entity) {
    return modelMapper.map(entity, ProjectDto.class);
  }

  @Override
  public ProjectEntity toEntity(ProjectDto dto) {
    return modelMapper.map(dto, ProjectEntity.class);
  }

  @Override
  public ProjectExtendedDto toExtendedDto(
      ProjectEntity entity, List<PersonDto> personDtos, List<SkillDto> skillDtos) {

    return ProjectExtendedDto.builder()
        .id(entity.getId())
        .projectName(entity.getProjectName())
        .people(personDtos)
        .skills(skillDtos)
        .build();
  }
}
