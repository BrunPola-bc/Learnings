package com.brunpola.cv_management.controllers;

import com.brunpola.cv_management.domain.dto.PersonDto;
import com.brunpola.cv_management.domain.entities.PersonEntity;
import com.brunpola.cv_management.mappers.Mapper;
import com.brunpola.cv_management.services.PersonService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController {

  private final PersonService personService;
  private final Mapper<PersonEntity, PersonDto> personMapper;

  public PersonController(
      PersonService personService, Mapper<PersonEntity, PersonDto> personMapper) {
    this.personService = personService;
    this.personMapper = personMapper;
  }

  @PostMapping(path = "/people")
  public ResponseEntity<PersonDto> createPerson(@RequestBody PersonDto personDto) {
    PersonEntity personEntity = personMapper.mapFrom(personDto);
    PersonEntity savedPersonEntity = personService.save(personEntity);
    return new ResponseEntity<>(personMapper.mapTo(savedPersonEntity), HttpStatus.CREATED);
  }

  @GetMapping(path = "/people")
  public List<PersonDto> listPeople() {
    List<PersonEntity> people = personService.findAll();
    return people.stream().map(personMapper::mapTo).collect(Collectors.toList());
  }

  @GetMapping(path = "/people/{id}")
  public ResponseEntity<PersonDto> getPerson(@PathVariable("id") Long id) {
    Optional<PersonEntity> foundPerson = personService.findOne(id);
    return foundPerson
        .map(
            personEntity -> {
              PersonDto personDto = personMapper.mapTo(personEntity);
              return new ResponseEntity<>(personDto, HttpStatus.OK);
            })
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PutMapping("people/{id}")
  public ResponseEntity<PersonDto> fullUpdatePerson(
      @PathVariable("id") Long id, @RequestBody PersonDto personDto) {

    if (!personService.isExists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    personDto.setId(id);
    PersonEntity personEntity = personMapper.mapFrom(personDto);
    PersonEntity savedPersonEntity = personService.save(personEntity);

    return new ResponseEntity<>(personMapper.mapTo(savedPersonEntity), HttpStatus.OK);
  }

  // @PatchMapping(path = "people/{id}")
  // public ResponseEntity<PersonDto> patchPerson(@PathVariable("id") Long id) {
  //   // TODO: process PATCH request

  //   return entity;
  // }

  @DeleteMapping(path = "people/{id}")
  public void deletePerson(@PathVariable("id") Long id) {
    // TODO: process DELETE request
  }
}
