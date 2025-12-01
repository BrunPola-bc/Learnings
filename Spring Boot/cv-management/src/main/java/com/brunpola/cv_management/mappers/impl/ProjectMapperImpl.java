package com.brunpola.cv_management.mappers.impl;

import com.brunpola.cv_management.domain.dto.ProjectDto;
import com.brunpola.cv_management.domain.entities.ProjectEntity;
import com.brunpola.cv_management.mappers.Mapper;
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
