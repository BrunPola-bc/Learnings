package com.brunpola.cv_management.mappers.impl;

import com.brunpola.cv_management.domain.dto.PersonExtendedDto;
import com.brunpola.cv_management.domain.entities.PersonEntity;
import com.brunpola.cv_management.mappers.ExtendedMapper;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PersonExtendedMapperImpl implements ExtendedMapper<PersonEntity, PersonExtendedDto> {

  private final SkillMapperImpl skillMapper;
  private final ProjectMapperImpl projectMapper;

  public PersonExtendedMapperImpl(SkillMapperImpl skillMapper, ProjectMapperImpl projectMapper) {
    this.skillMapper = skillMapper;
    this.projectMapper = projectMapper;
  }

  @Override
  public PersonExtendedDto mapToExtended(PersonEntity person) {
    return PersonExtendedDto.builder()
        .id(person.getId())
        .firstName(person.getFirstName())
        .lastName(person.getLastName())
        .skills(
            person.getSkills().stream()
                .map(ps -> skillMapper.mapTo(ps.getSkill()))
                .collect(Collectors.toList()))
        .projects(
            person.getProjects().stream()
                .map(pp -> projectMapper.mapTo(pp.getProject()))
                .collect(Collectors.toList()))
        .build();
  }
}
