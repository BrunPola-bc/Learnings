package com.brunpola.cv_management.services;

import com.brunpola.cv_management.domain.entities.PersonEntity;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

public interface PersonService {

  @Validated
  PersonEntity save(@Valid PersonEntity person);

  PersonEntity update(PersonEntity person);

  List<PersonEntity> findAll();

  Page<PersonEntity> findAll(Pageable pageable);

  PersonEntity findOne(Long id);

  boolean isExists(Long id);

  PersonEntity partialUpdate(Long id, PersonEntity personEntity);

  void delete(Long id);

  List<PersonEntity> search(PersonEntity personEntity);
}
