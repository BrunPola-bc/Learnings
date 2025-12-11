package com.brunpola.rest_to_my_db.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brunpola.rest_to_my_db.domain.Person;
import com.brunpola.rest_to_my_db.services.PersonService;

@RestController
@RequestMapping("/people")
public class PersonController {

  private final PersonService personService;

  public PersonController(PersonService personService) {
    this.personService = personService;
  }

  @GetMapping("/")
  public List<Person> getAllPeople() {
    return personService.getAllPeople();
  }

  @GetMapping("/{id}")
  public Person getPersonById(@PathVariable Long id) {
    return personService.getPersonById(id);
  }

  @PostMapping("/")
  public Person createPerson(@RequestBody Person person) {
    return personService.createPerson(person);
  }

  @PutMapping("/{id}")
  public Person updatePerson(@PathVariable Long id, @RequestBody Person person) {
    return personService.updatePerson(id, person);
  }

  @PatchMapping("/{id}")
  public Person partialUpdate(@PathVariable Long id, @RequestBody Person person) {
    return personService.partialUpdatePerson(id, person);
  }

  @DeleteMapping("/{id}")
  public void deletePerson(@PathVariable Long id) {
    personService.deletePerson(id);
  }
}
