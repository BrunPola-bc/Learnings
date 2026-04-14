package com.brunpola.people_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PersonNotFoundException extends NotFoundException {

  public PersonNotFoundException(Long id) {
    super("Person with ID " + id + " not found");
  }
}
