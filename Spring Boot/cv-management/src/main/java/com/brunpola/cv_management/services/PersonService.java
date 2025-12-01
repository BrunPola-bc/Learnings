package com.brunpola.cv_management.services;

import com.brunpola.cv_management.domain.entities.PersonEntity;
import java.util.List;
import java.util.Optional;

public interface PersonService {

  PersonEntity save(PersonEntity person);

  List<PersonEntity> findAll();

  Optional<PersonEntity> findOne(Long id);

  boolean isExists(Long id);
}
