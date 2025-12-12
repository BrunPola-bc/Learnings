package com.brunpola.rest_to_my_db.services;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import com.brunpola.rest_to_my_db.domain.Person;

@HttpExchange(/* url = "${CV_MANAGEMENT_BASE_URL}", */ accept = "application/json")
public interface PersonService {

  @GetExchange("/people/")
  List<Person> getAllPeople();

  @GetExchange("/people/{id}")
  Person getPersonById(@PathVariable("id") Long id);

  @PostExchange("/people/")
  Person createPerson(@RequestBody Person person);

  @PutExchange("/people/{id}")
  Person updatePerson(@PathVariable("id") Long id, @RequestBody Person person);

  @PatchExchange("/people/{id}")
  Person partialUpdatePerson(@PathVariable("id") Long id, @RequestBody Person person);

  @DeleteExchange("/people/{id}")
  void deletePerson(@PathVariable("id") Long id);
}
