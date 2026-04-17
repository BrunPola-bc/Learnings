package com.brunpola.people_service.controller;

import com.brunpola.people_service.domain.dto.IdsRequestDto;
import com.brunpola.people_service.domain.dto.PersonDto;
import com.brunpola.people_service.domain.dto.PersonExtendedDto;
import com.brunpola.people_service.service.PersonService;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/people")
public class PersonController {

  private final PersonService personService;

  public PersonController(PersonService personService) {
    this.personService = personService;
  }

  @PostMapping
  public ResponseEntity<PersonDto> createPerson(@RequestBody @Valid PersonDto personDto) {
    PersonDto savedPersonDto = personService.save(personDto);
    return new ResponseEntity<>(savedPersonDto, HttpStatus.CREATED);
  }

  @GetMapping
  public List<PersonDto> listPeople() {
    return personService.findAll();
  }

  @GetMapping(path = "/{id}")
  public PersonDto getPerson(@PathVariable("id") Long id) {
    return personService.findOne(id);
  }

  @PutMapping("/{id}")
  public PersonDto fullUpdatePerson(@PathVariable("id") Long id, @RequestBody PersonDto personDto) {

    personDto.setId(id);
    return personService.update(personDto);
  }

  @PatchMapping(path = "/{id}")
  public PersonDto partialUpdatePerson(
      @PathVariable("id") Long id, @RequestBody PersonDto personDto) {

    return personService.partialUpdate(id, personDto);
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> deletePerson(@PathVariable("id") Long id) {
    personService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/extended")
  public List<PersonExtendedDto> listPeopleExtended() {
    return personService.findAllExtended();
  }

  @GetMapping("/{id}/extended")
  public PersonExtendedDto getPersonExtended(@PathVariable("id") Long id) {
    return personService.findOneExtended(id);
  }

  @PutMapping("/{id}/skills")
  public PersonDto updateSkills(@PathVariable Long id, @RequestBody IdsRequestDto request) {
    return personService.updateSkills(id, request.getIds());
  }

  @PutMapping("/{id}/projects")
  public PersonDto updateProjects(@PathVariable Long id, @RequestBody IdsRequestDto request) {
    return personService.updateProjects(id, request.getIds());
  }

  @GetMapping("/by-project/{projectId}")
  public List<PersonDto> getPeopleByProjectId(@PathVariable Long projectId) {
    return personService.findByProjectId(projectId);
  }

  @GetMapping("/by-skill/{skillId}")
  public List<PersonDto> getPeopleBySkillId(@PathVariable Long skillId) {
    return personService.findBySkillId(skillId);
  }

  @GetMapping("/by-ids")
  public List<PersonDto> getPeopleByIds(@RequestParam List<Long> ids) {
    return personService.findByIds(ids);
  }
}
