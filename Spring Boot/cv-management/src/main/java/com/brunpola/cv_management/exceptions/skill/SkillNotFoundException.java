package com.brunpola.cv_management.exceptions.skill;

import com.brunpola.cv_management.exceptions.base.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** TEST */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class SkillNotFoundException extends NotFoundException {

  /**
   * TEST
   *
   * @param id the ID that doesn't have a corresponding skill
   */
  public SkillNotFoundException(Long id) {
    super("Skill with ID " + id + " not found");
  }
}
