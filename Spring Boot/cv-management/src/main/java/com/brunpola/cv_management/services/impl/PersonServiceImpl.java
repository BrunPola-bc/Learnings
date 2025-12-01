package com.brunpola.cv_management.services.impl;

import com.brunpola.cv_management.domain.entities.PersonEntity;
import com.brunpola.cv_management.repositories.PersonRepository;
import com.brunpola.cv_management.services.PersonService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl implements PersonService {

  private final PersonRepository personRepository;

  public PersonServiceImpl(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  @Override
  public PersonEntity save(PersonEntity person) {
    return personRepository.save(person);
  }

  @Override
  public List<PersonEntity> findAll() {
    return StreamSupport.stream(personRepository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<PersonEntity> findOne(Long id) {
    return personRepository.findById(id);
  }

  @Override
  public boolean isExists(Long id) {
    return personRepository.existsById(id);
  }
}
