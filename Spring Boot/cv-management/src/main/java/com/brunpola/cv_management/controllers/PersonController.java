package com.brunpola.cv_management.controllers;

import com.brunpola.cv_management.domain.dto.PersonDto;
import com.brunpola.cv_management.domain.dto.PersonExtendedDto;
import com.brunpola.cv_management.domain.entities.PersonEntity;
import com.brunpola.cv_management.mappers.ExtendedMapper;
import com.brunpola.cv_management.mappers.Mapper;
import com.brunpola.cv_management.services.PersonService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link PersonEntity} resources. Provides endpoints for CRUD
 * operations and mapping to DTOs.
 */
@RestController
@RequestMapping(path = "/people")
public class PersonController {

  private final PersonService personService;
  private final Mapper<PersonEntity, PersonDto> personMapper;
  private final ExtendedMapper<PersonEntity, PersonExtendedDto> personExtendedMapper;

  /**
   * Constructs a {@link PersonController} with required services and mappers.
   *
   * @param personService service for CRUD operations on PersonEntity
   * @param personMapper mapper for PersonEntity &lt;-&gt; PersonDto conversion
   * @param personExtendedMapper mapper for PersonEntity &lt;-&gt; PersonExtendedDto conversion
   */
  public PersonController(
      PersonService personService,
      Mapper<PersonEntity, PersonDto> personMapper,
      ExtendedMapper<PersonEntity, PersonExtendedDto> personExtendedMapper) {
    this.personService = personService;
    this.personMapper = personMapper;
    this.personExtendedMapper = personExtendedMapper;
  }

  /**
   * Creates a new person resource.
   *
   * @param personDto DTO containing data for the new person
   * @return {@link ResponseEntity} containing the saved {@link PersonDto} and HTTP status CREATED
   */
  @PostMapping
  public ResponseEntity<PersonDto> createPerson(@RequestBody PersonDto personDto) {
    PersonEntity personEntity = personMapper.mapFrom(personDto);
    PersonEntity savedPersonEntity = personService.save(personEntity);
    return new ResponseEntity<>(personMapper.mapTo(savedPersonEntity), HttpStatus.CREATED);
  }

  /**
   * Lists all people as {@link PersonDto}.
   *
   * @param pageable optional pagination information (currently not applied in mapping)
   * @return list of all people mapped to DTOs
   */
  @GetMapping
  public List<PersonDto> listPeople(Pageable pageable) {
    List<PersonEntity> people = personService.findAll();
    return people.stream().map(personMapper::mapTo).collect(Collectors.toList());
  }

  /**
   * Retrieves a single person by ID.
   *
   * @param id unique identifier of the person
   * @return the mapped {@link PersonDto} of the found person
   */
  @GetMapping(path = "/{id}")
  public PersonDto getPerson(@PathVariable("id") Long id) {
    PersonEntity foundPerson = personService.findOne(id);
    return personMapper.mapTo(foundPerson);
  }

  /**
   * Searches for people matching criteria in a {@link PersonDto}.
   *
   * @param personDto DTO containing search criteria
   * @return list of matching {@link PersonDto} objects
   */
  @GetMapping("/search")
  public List<PersonDto> searchPeople(/* @RequestBody */ PersonDto personDto) {
    PersonEntity personEntity = personMapper.mapFrom(personDto);
    return personService.search(personEntity).stream().map(personMapper::mapTo).toList();
  }

  /**
   * Performs a full update of a person resource.
   *
   * @param id the ID of the person to update
   * @param personDto DTO containing updated data
   * @return the updated {@link PersonDto}
   */
  @PutMapping("/{id}")
  public PersonDto fullUpdatePerson(@PathVariable("id") Long id, @RequestBody PersonDto personDto) {

    // if (!personService.isExists(id)) {
    // return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    // }

    personDto.setId(id);
    PersonEntity personEntity = personMapper.mapFrom(personDto);
    PersonEntity updatedPersonEntity = personService.update(personEntity);

    return personMapper.mapTo(updatedPersonEntity);
  }

  /**
   * Performs a partial update of a person resource.
   *
   * @param id the ID of the person to update
   * @param personDto DTO containing partial updated data
   * @return the updated {@link PersonDto}
   */
  @PatchMapping(path = "/{id}")
  public PersonDto partialUpdatePerson(
      @PathVariable("id") Long id, @RequestBody PersonDto personDto) {

    PersonEntity personEntity = personMapper.mapFrom(personDto);
    PersonEntity savedPersonEntity = personService.partialUpdate(id, personEntity);

    return personMapper.mapTo(savedPersonEntity);
  }

  /**
   * Deletes a person by ID.
   *
   * @param id the ID of the person to delete
   * @return {@link ResponseEntity} with HTTP status NO_CONTENT
   */
  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> deletePerson(@PathVariable("id") Long id) {
    personService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Lists all people with extended information as {@link PersonExtendedDto}.
   *
   * @return list of extended DTOs for all people
   */
  @GetMapping("/extended")
  public List<PersonExtendedDto> listPeopleExtended() {
    List<PersonEntity> people = personService.findAll();
    return people.stream().map(personExtendedMapper::mapToExtended).toList();
  }

  /**
   * Retrieves a single person with extended information by ID.
   *
   * @param id the ID of the person to retrieve
   * @return the extended DTO of the found person
   */
  @GetMapping("/{id}/extended")
  public PersonExtendedDto getPersonExtended(@PathVariable("id") Long id) {
    PersonEntity foundPerson = personService.findOne(id);
    return personExtendedMapper.mapToExtended(foundPerson);
  }
}
