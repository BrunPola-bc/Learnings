package com.brunpola.cv_management.controllers;

import com.brunpola.cv_management.domain.dto.PersonDto;
import com.brunpola.cv_management.domain.dto.PersonExtendedDto;
import com.brunpola.cv_management.domain.entities.PersonEntity;
import com.brunpola.cv_management.mappers.ExtendedMapper;
import com.brunpola.cv_management.mappers.Mapper;
import com.brunpola.cv_management.services.PersonService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/people")
public class PersonController {

  private final PersonService personService;
  private final Mapper<PersonEntity, PersonDto> personMapper;
  private final ExtendedMapper<PersonEntity, PersonExtendedDto> personExtendedMapper;

  public PersonController(
      PersonService personService,
      Mapper<PersonEntity, PersonDto> personMapper,
      ExtendedMapper<PersonEntity, PersonExtendedDto> personExtendedMapper) {
    this.personService = personService;
    this.personMapper = personMapper;
    this.personExtendedMapper = personExtendedMapper;
  }

  @PostMapping(path = "")
  public ResponseEntity<PersonDto> createPerson(@RequestBody PersonDto personDto) {
    PersonEntity personEntity = personMapper.mapFrom(personDto);
    PersonEntity savedPersonEntity = personService.save(personEntity);
    return new ResponseEntity<>(personMapper.mapTo(savedPersonEntity), HttpStatus.CREATED);
  }

  @GetMapping(path = "")
  public Page<PersonDto> listPeople(Pageable pageable) {
    Page<PersonEntity> people = personService.findAll(pageable);
    return people.map(personMapper::mapTo);
  }

  @GetMapping(path = "/{id}")
  public PersonDto getPerson(@PathVariable("id") Long id) {
    PersonEntity foundPerson = personService.findOne(id);
    return personMapper.mapTo(foundPerson);
  }

  @GetMapping("/search")
  public List<PersonDto> searchPeople(/*@RequestBody*/ PersonDto personDto) {
    PersonEntity personEntity = personMapper.mapFrom(personDto);
    return personService.search(personEntity).stream().map(personMapper::mapTo).toList();
  }

  @PutMapping("/{id}")
  public PersonDto fullUpdatePerson(@PathVariable("id") Long id, @RequestBody PersonDto personDto) {

    // if (!personService.isExists(id)) {
    //   return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    // }

    personDto.setId(id);
    PersonEntity personEntity = personMapper.mapFrom(personDto);
    PersonEntity updatedPersonEntity = personService.update(personEntity);

    return personMapper.mapTo(updatedPersonEntity);
  }

  @PatchMapping(path = "/{id}")
  public PersonDto partialUpdatePerson(
      @PathVariable("id") Long id, @RequestBody PersonDto personDto) {

    PersonEntity personEntity = personMapper.mapFrom(personDto);
    PersonEntity savedPersonEntity = personService.partialUpdate(id, personEntity);

    return personMapper.mapTo(savedPersonEntity);
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> deletePerson(@PathVariable("id") Long id) {
    personService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/extended")
  public List<PersonExtendedDto> listPeopleExtended() {
    List<PersonEntity> people = personService.findAll();
    return people.stream().map(personExtendedMapper::mapToExtended).toList();
  }

  @GetMapping("{id}/extended")
  public PersonExtendedDto getPersonExtended(@PathVariable("id") Long id) {
    PersonEntity foundPerson = personService.findOne(id);
    return personExtendedMapper.mapToExtended(foundPerson);
  }
}
