package com.brunpola.people_service;

import com.brunpola.people_service.domain.dto.PersonDto;
import com.brunpola.people_service.domain.dto.PersonExtendedDto;
import com.brunpola.people_service.domain.entity.PersonEntity;
import com.brunpola.people_service.domain.external.ProjectDto;
import com.brunpola.people_service.domain.external.SkillDto;
import java.util.ArrayList;
import java.util.List;

public class TestDataUtil {

  public static PersonDto samplePersonDto(boolean withId) {
    PersonDto personDto = PersonDto.builder().firstName("Person").lastName("Last Name").build();
    if (withId) {
      personDto.setId(1L);
    }
    return personDto;
  }

  public static PersonEntity samplePersonEntity(boolean withId) {
    PersonEntity personEntity =
        PersonEntity.builder()
            .firstName("Person")
            .lastName("Last Name")
            .projectIds(List.of(1L))
            .skillIds(List.of(1L))
            .build();
    if (withId) {
      personEntity.setId(1L);
    }
    return personEntity;
  }

  public static List<PersonDto> samplePeopleDtos(boolean withIds, int count) {
    List<PersonDto> peopleDtos = new ArrayList<>();
    for (Long i = 1L; i <= count; i++) {
      PersonDto personDto =
          PersonDto.builder().firstName("Person " + i).lastName("Last Name " + i).build();
      if (withIds) {
        personDto.setId(i);
      }
      peopleDtos.add(personDto);
    }
    return peopleDtos;
  }

  public static List<PersonEntity> samplePeopleEntities(boolean withIds, int count) {
    List<PersonEntity> peopleEntities = new ArrayList<>();
    for (Long i = 1L; i <= count; i++) {
      PersonEntity personEntity =
          PersonEntity.builder().firstName("Person " + i).lastName("Last Name " + i).build();
      if (withIds) {
        personEntity.setId(i);
      }
      peopleEntities.add(personEntity);
    }
    return peopleEntities;
  }

  public static ProjectDto sampleProjectDto() {
    return ProjectDto.builder().id(1L).projectName("Project").build();
  }

  public static SkillDto sampleSkillDto() {
    return SkillDto.builder().id(1L).skillName("Skill").build();
  }

  public static PersonExtendedDto samplePersonExtendedDto(boolean withId) {
    PersonExtendedDto personExtendedDto =
        PersonExtendedDto.builder()
            .firstName("Person")
            .lastName("Last Name")
            .projects(List.of(sampleProjectDto()))
            .skills(List.of(sampleSkillDto()))
            .build();
    if (withId) {
      personExtendedDto.setId(1L);
    }
    return personExtendedDto;
  }

  public static PersonEntity samplePersonEntityWithEmptyLists(boolean withId) {

    PersonEntity personEntity =
        PersonEntity.builder()
            .firstName("Person")
            .lastName("Last Name")
            .projectIds(List.of())
            .skillIds(List.of())
            .build();
    if (withId) {
      personEntity.setId(1L);
    }
    return personEntity;
  }

  public static PersonExtendedDto samplePersonExtendedDtoWithEmptyLists(boolean withId) {
    PersonExtendedDto personExtendedDto =
        PersonExtendedDto.builder()
            .firstName("Person")
            .lastName("Last Name")
            .projects(List.of())
            .skills(List.of())
            .build();
    if (withId) {
      personExtendedDto.setId(1L);
    }
    return personExtendedDto;
  }
}
