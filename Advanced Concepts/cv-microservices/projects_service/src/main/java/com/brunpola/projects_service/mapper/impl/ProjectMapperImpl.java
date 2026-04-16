package com.brunpola.projects_service.mapper.impl;

import com.brunpola.projects_service.domain.dto.ProjectDto;
import com.brunpola.projects_service.domain.entity.ProjectEntity;
import com.brunpola.projects_service.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapperImpl implements Mapper<ProjectEntity, ProjectDto> {

  private final ModelMapper modelMapper;

  public ProjectMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public ProjectDto mapTo(ProjectEntity projectEntity) {
    return modelMapper.map(projectEntity, ProjectDto.class);
  }

  @Override
  public ProjectEntity mapFrom(ProjectDto projectDto) {
    return modelMapper.map(projectDto, ProjectEntity.class);
  }
}
