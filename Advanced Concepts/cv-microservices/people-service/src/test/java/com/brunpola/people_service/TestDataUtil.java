package com.brunpola.people_service;

import com.brunpola.people_service.domain.dto.PersonDto;
import com.brunpola.people_service.domain.dto.PersonExtendedDto;
import com.brunpola.people_service.domain.entity.PersonEntity;
import com.brunpola.people_service.domain.external.ProjectDto;
import com.brunpola.people_service.domain.external.SkillDto;
import com.brunpola.people_service.service.JwtUtil;
import io.jsonwebtoken.Jwts;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;

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

  public static List<PersonExtendedDto> samplePeopleExtendedDtos(boolean withIds, int count) {
    List<PersonExtendedDto> peopleDtos = new ArrayList<>();
    for (Long i = 1L; i <= count; i++) {
      PersonExtendedDto personDto =
          PersonExtendedDto.builder()
              .firstName("Person " + i)
              .lastName("Last Name " + i)
              .projects(List.of(sampleProjectDto()))
              .skills(List.of(sampleSkillDto()))
              .build();
      if (withIds) {
        personDto.setId(i);
      }
      peopleDtos.add(personDto);
    }
    return peopleDtos;
  }

  public static UserDetails dummyUser() {
    return org.springframework.security.core.userdetails.User.withUsername("test-user")
        .password("password")
        .authorities("ROLE_USER")
        .build();
  }

  public static String getExpiredToken(UserDetails user, JwtUtil jwtUtil) {
    return Jwts.builder()
        .subject(user.getUsername())
        .issuedAt(new Date(System.currentTimeMillis() - 2 * JwtUtil.ONE_DAY_IN_MILLIS))
        .expiration(new Date(System.currentTimeMillis() - JwtUtil.ONE_DAY_IN_MILLIS))
        .signWith(jwtUtil.getSigningKey(), Jwts.SIG.HS256)
        .compact();
  }
}
