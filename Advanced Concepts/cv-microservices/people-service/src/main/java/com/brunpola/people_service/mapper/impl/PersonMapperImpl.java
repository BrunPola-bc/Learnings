package com.brunpola.people_service.mapper.impl;

import com.brunpola.people_service.domain.dto.PersonDto;
import com.brunpola.people_service.domain.dto.PersonExtendedDto;
import com.brunpola.people_service.domain.entity.PersonEntity;
import com.brunpola.people_service.domain.external.ProjectDto;
import com.brunpola.people_service.domain.external.SkillDto;
import com.brunpola.people_service.mapper.PersonMapper;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PersonMapperImpl implements PersonMapper {

  private final ModelMapper modelMapper;

  public PersonMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public PersonDto toDto(PersonEntity entity) {
    return modelMapper.map(entity, PersonDto.class);
  }

  @Override
  public PersonEntity toEntity(PersonDto dto) {
    return modelMapper.map(dto, PersonEntity.class);
  }

  @Override
  public PersonExtendedDto toExtendedDto(
      PersonEntity entity, List<ProjectDto> projectDtos, List<SkillDto> skillDtos) {

    return PersonExtendedDto.builder()
        .id(entity.getId())
        .firstName(entity.getFirstName())
        .lastName(entity.getLastName())
        .projects(projectDtos)
        .skills(skillDtos)
        .build();
  }
}
