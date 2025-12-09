package com.brunpola.cv_management.exceptions.person;

import com.brunpola.cv_management.exceptions.base.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PersonNotFoundException extends NotFoundException {
  public PersonNotFoundException(Long id) {
    super("Person with ID " + id + " not found");
  }
}
