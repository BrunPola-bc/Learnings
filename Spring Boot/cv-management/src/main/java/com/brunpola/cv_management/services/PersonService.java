package com.brunpola.cv_management.services;

import com.brunpola.cv_management.domain.entities.PersonEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PersonService {

  PersonEntity save(PersonEntity person);

  List<PersonEntity> findAll();

  Page<PersonEntity> findAll(Pageable pageable);

  Optional<PersonEntity> findOne(Long id);

  boolean isExists(Long id);

  PersonEntity partialUpdate(Long id, PersonEntity personEntity);

  void delete(Long id);
}
