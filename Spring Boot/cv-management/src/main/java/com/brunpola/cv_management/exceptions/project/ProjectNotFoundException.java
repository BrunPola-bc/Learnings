package com.brunpola.cv_management.exceptions.project;

import com.brunpola.cv_management.exceptions.base.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProjectNotFoundException extends NotFoundException {
  public ProjectNotFoundException(Long id) {
    super("Project with ID " + id + " not found");
  }
}
