package com.brunpola.cv_management.exceptions.person;

import com.brunpola.cv_management.exceptions.base.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a Person resource cannot be found.
 *
 * <p>Returns HTTP 404 (Not Found) when thrown from a controller.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PersonNotFoundException extends NotFoundException {

  /**
   * Creates an exception for a missing person with the given ID.
   *
   * @param id identifier of the missing person
   */
  public PersonNotFoundException(Long id) {
    super("Person with ID " + id + " not found");
  }
}
