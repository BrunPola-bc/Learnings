package com.brunpola.people_service.service.impl;

import com.brunpola.people_service.client.ProjectHttpClient;
import com.brunpola.people_service.client.SkillHttpClient;
import com.brunpola.people_service.domain.dto.PersonDto;
import com.brunpola.people_service.domain.dto.PersonExtendedDto;
import com.brunpola.people_service.domain.entity.PersonEntity;
import com.brunpola.people_service.domain.external.ProjectDto;
import com.brunpola.people_service.domain.external.SkillDto;
import com.brunpola.people_service.exception.PersonNotFoundException;
import com.brunpola.people_service.mapper.PersonMapper;
import com.brunpola.people_service.repository.PersonRepository;
import com.brunpola.people_service.service.PersonService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl implements PersonService {

  private final PersonRepository personRepository;
  private final PersonMapper personMapper;
  private final ProjectHttpClient projectClient;
  private final SkillHttpClient skillClient;

  public PersonServiceImpl(
      PersonRepository personRepository,
      PersonMapper personMapper,
      ProjectHttpClient projectClient,
      SkillHttpClient skillClient) {
    this.personRepository = personRepository;
    this.personMapper = personMapper;
    this.projectClient = projectClient;
    this.skillClient = skillClient;
  }

  @Override
  public PersonDto save(PersonDto personDto) {
    PersonEntity personEntity = personMapper.toEntity(personDto);
    personEntity = personRepository.save(personEntity);
    return personMapper.toDto(personEntity);
  }

  @Override
  public PersonDto update(PersonDto personDto) {
    if (!isExists(personDto.getId())) {
      throw new PersonNotFoundException(personDto.getId());
    }

    PersonEntity personEntity = personMapper.toEntity(personDto);
    personEntity = personRepository.save(personEntity);
    return personMapper.toDto(personEntity);
  }

  @Override
  public List<PersonDto> findAll() {
    return personRepository.findAll().stream().map(personMapper::toDto).toList();
  }

  @Override
  public PersonDto findOne(Long id) {
    PersonEntity personEntity =
        personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
    return personMapper.toDto(personEntity);
  }

  @Override
  public boolean isExists(Long id) {
    return personRepository.existsById(id);
  }

  @Override
  public PersonDto partialUpdate(Long id, PersonDto personDto) {

    personDto.setId(id);

    PersonEntity updatedPersonEntity =
        personRepository
            .findById(id)
            .map(
                existingPerson -> {
                  Optional.ofNullable(personDto.getFirstName())
                      .ifPresent(existingPerson::setFirstName);
                  Optional.ofNullable(personDto.getLastName())
                      .ifPresent(existingPerson::setLastName);
                  return personRepository.save(existingPerson);
                })
            .orElseThrow(() -> new PersonNotFoundException(id));

    return personMapper.toDto(updatedPersonEntity);
  }

  @Override
  public void delete(Long id) {
    if (!isExists(id)) {
      throw new PersonNotFoundException(id);
    }
    personRepository.deleteById(id);
  }

  @Override
  public PersonDto updateSkills(Long personId, List<Long> skillIds) {
    PersonEntity person =
        personRepository
            .findById(personId)
            .orElseThrow(() -> new PersonNotFoundException(personId));

    person.setSkillIds(skillIds);

    PersonEntity saved = personRepository.save(person);
    return personMapper.toDto(saved);
  }

  @Override
  public PersonDto updateProjects(Long personId, List<Long> projectIds) {
    PersonEntity person =
        personRepository
            .findById(personId)
            .orElseThrow(() -> new PersonNotFoundException(personId));

    person.setProjectIds(projectIds);

    PersonEntity saved = personRepository.save(person);
    return personMapper.toDto(saved);
  }

  @Override
  public List<PersonDto> findByProjectId(Long projectId) {
    List<PersonEntity> byProjectIdsContaining =
        personRepository.findByProjectIdsContaining(projectId);
    return byProjectIdsContaining.stream().map(personMapper::toDto).toList();
  }

  @Override
  public List<PersonDto> findBySkillId(Long skillId) {
    List<PersonEntity> bySkillIdsContaining = personRepository.findBySkillIdsContaining(skillId);
    return bySkillIdsContaining.stream().map(personMapper::toDto).toList();
  }

  @Override
  public PersonExtendedDto findOneExtended(Long id) {
    PersonEntity personEntity =
        personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));

    List<Long> projectIds = personEntity.getProjectIds();
    List<ProjectDto> projects =
        projectIds.isEmpty() ? List.of() : projectClient.getProjectsByIds(projectIds);

    List<Long> skillIds = personEntity.getSkillIds();
    List<SkillDto> skills = skillIds.isEmpty() ? List.of() : skillClient.getSkillsByIds(skillIds);

    return personMapper.toExtendedDto(personEntity, projects, skills);
  }

  @Override
  public List<PersonExtendedDto> findAllExtended() {

    List<PersonEntity> personEntities = personRepository.findAll();
    Set<Long> allProjectIds =
        personEntities.stream()
            .flatMap(entity -> entity.getProjectIds().stream())
            .collect(Collectors.toSet());

    Set<Long> allSkillIds =
        personEntities.stream()
            .flatMap(entity -> entity.getSkillIds().stream())
            .collect(Collectors.toSet());

    Map<Long, ProjectDto> projectDtoMap =
        projectClient.getProjectsByIds(new ArrayList<>(allProjectIds)).stream()
            .collect(Collectors.toMap(ProjectDto::getId, project -> project));

    Map<Long, SkillDto> skillDtoMap =
        skillClient.getSkillsByIds(new ArrayList<>(allSkillIds)).stream()
            .collect(Collectors.toMap(SkillDto::getId, skill -> skill));

    return personEntities.stream()
        .map(
            personEntity -> {
              List<ProjectDto> projects =
                  personEntity.getProjectIds().stream()
                      .map(projectDtoMap::get)
                      .filter(Objects::nonNull)
                      .toList();

              List<SkillDto> skills =
                  personEntity.getSkillIds().stream()
                      .map(skillDtoMap::get)
                      .filter(Objects::nonNull)
                      .toList();

              return personMapper.toExtendedDto(personEntity, projects, skills);
            })
        .toList();
  }

  @Override
  public List<PersonDto> findByIds(List<Long> ids) {
    List<PersonEntity> people = personRepository.findAllById(ids);
    return people.stream().map(personMapper::toDto).toList();
  }
}
