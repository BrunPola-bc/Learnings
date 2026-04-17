package com.brunpola.skills_service.mapper.impl;

import com.brunpola.skills_service.domain.dto.SkillDto;
import com.brunpola.skills_service.domain.dto.SkillExtendedDto;
import com.brunpola.skills_service.domain.entity.SkillEntity;
import com.brunpola.skills_service.domain.external.PersonDto;
import com.brunpola.skills_service.domain.external.ProjectDto;
import com.brunpola.skills_service.mapper.SkillMapper;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class SkillMapperImpl implements SkillMapper {

  private final ModelMapper modelMapper;

  public SkillMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public SkillDto toDto(SkillEntity entity) {
    return modelMapper.map(entity, SkillDto.class);
  }

  @Override
  public SkillEntity toEntity(SkillDto dto) {
    return modelMapper.map(dto, SkillEntity.class);
  }

  @Override
  public SkillExtendedDto toExtendedDto(
      SkillEntity entity, List<PersonDto> personDtos, List<ProjectDto> projectDtos) {

    return SkillExtendedDto.builder()
        .id(entity.getId())
        .skillName(entity.getSkillName())
        .people(personDtos)
        .projects(projectDtos)
        .build();
  }
}
