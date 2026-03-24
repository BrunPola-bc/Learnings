package com.brunpola.cv_management.mappers.impl;

import com.brunpola.cv_management.domain.dto.PersonExtendedDto;
import com.brunpola.cv_management.domain.entities.PersonEntity;
import com.brunpola.cv_management.mappers.ExtendedMapper;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link ExtendedMapper} for {@link PersonEntity} and {@link PersonExtendedDto}.
 * Converts a {@link PersonEntity} into an extended DTO including its {@link
 * com.brunpola.cv_management.domain.dto.SkillDto} and {@link
 * com.brunpola.cv_management.domain.dto.ProjectDto}.
 */
@Component
public class PersonExtendedMapperImpl implements ExtendedMapper<PersonEntity, PersonExtendedDto> {

  private final SkillMapperImpl skillMapper;
  private final ProjectMapperImpl projectMapper;

  /**
   * Constructs a PersonExtendedMapperImpl with the given skill and project mappers.
   *
   * @param skillMapper mapper for converting skills from entity to DTO
   * @param projectMapper mapper for converting projects from entity to DTO
   */
  public PersonExtendedMapperImpl(SkillMapperImpl skillMapper, ProjectMapperImpl projectMapper) {
    this.skillMapper = skillMapper;
    this.projectMapper = projectMapper;
  }

  /** {@inheritDoc} */
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
