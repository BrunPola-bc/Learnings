package com.brunpola.people_service.service.impl;

import com.brunpola.people_service.domain.dto.PersonDto;
import com.brunpola.people_service.domain.dto.PersonExtendedDto;
import com.brunpola.people_service.domain.entity.PersonEntity;
import com.brunpola.people_service.exception.PersonNotFoundException;
import com.brunpola.people_service.mapper.ExtendedMapper;
import com.brunpola.people_service.mapper.Mapper;
import com.brunpola.people_service.repository.PersonRepository;
import com.brunpola.people_service.service.PersonService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl implements PersonService {

  private final PersonRepository personRepository;
  private final Mapper<PersonEntity, PersonDto> personMapper;
  private final ExtendedMapper<PersonEntity, PersonExtendedDto> personExtendedMapper;

  public PersonServiceImpl(
      PersonRepository personRepository,
      Mapper<PersonEntity, PersonDto> personMapper,
      ExtendedMapper<PersonEntity, PersonExtendedDto> personExtendedMapper) {
    this.personRepository = personRepository;
    this.personMapper = personMapper;
    this.personExtendedMapper = personExtendedMapper;
  }

  @Override
  public PersonDto save(PersonDto personDto) {
    PersonEntity personEntity = personMapper.mapFrom(personDto);
    personEntity = personRepository.save(personEntity);
    return personMapper.mapTo(personEntity);
  }

  @Override
  public PersonDto update(PersonDto personDto) {
    if (!isExists(personDto.getId())) {
      throw new PersonNotFoundException(personDto.getId());
    }

    PersonEntity personEntity = personMapper.mapFrom(personDto);
    personEntity = personRepository.save(personEntity);
    return personMapper.mapTo(personEntity);
  }

  @Override
  public List<PersonDto> findAll() {
    return personRepository.findAll().stream().map(personMapper::mapTo).toList();
  }

  @Override
  public PersonDto findOne(Long id) {
    PersonEntity personEntity =
        personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
    return personMapper.mapTo(personEntity);
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

    return personMapper.mapTo(updatedPersonEntity);
  }

  @Override
  public void delete(Long id) {
    if (!isExists(id)) {
      throw new PersonNotFoundException(id);
    }
    personRepository.deleteById(id);
  }

  @Override
  public List<PersonExtendedDto> findAllExtended() {
    return personRepository.findAll().stream().map(personExtendedMapper::mapToExtended).toList();
  }

  @Override
  public PersonExtendedDto findOneExtended(Long id) {
    PersonEntity entity =
        personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));

    return personExtendedMapper.mapToExtended(entity);
  }

  @Override
  public PersonDto updateSkills(Long personId, List<Long> skillIds) {
    PersonEntity person =
        personRepository
            .findById(personId)
            .orElseThrow(() -> new PersonNotFoundException(personId));

    person.setSkillIds(skillIds);

    PersonEntity saved = personRepository.save(person);
    return personMapper.mapTo(saved);
  }

  @Override
  public PersonDto updateProjects(Long personId, List<Long> projectIds) {
    PersonEntity person =
        personRepository
            .findById(personId)
            .orElseThrow(() -> new PersonNotFoundException(personId));

    person.setProjectIds(projectIds);

    PersonEntity saved = personRepository.save(person);
    return personMapper.mapTo(saved);
  }
}
