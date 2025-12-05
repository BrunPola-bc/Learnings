package com.brunpola.cv_management.services.impl;

import com.brunpola.cv_management.domain.entities.PersonEntity;
import com.brunpola.cv_management.repositories.PersonRepository;
import com.brunpola.cv_management.services.PersonService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  // @Override
  // public List<PersonEntity> findAllExtended() {
  //   return StreamSupport.stream(personRepository.findAll().spliterator(), false)
  //       .collect(Collectors.toList());
  // }

  @Override
  public Page<PersonEntity> findAll(Pageable pageable) {
    return personRepository.findAll(pageable);
  }

  @Override
  public Optional<PersonEntity> findOne(Long id) {
    return personRepository.findById(id);
  }

  @Override
  public boolean isExists(Long id) {
    return personRepository.existsById(id);
  }

  @Override
  public PersonEntity partialUpdate(Long id, PersonEntity personEntity) {

    personEntity.setId(id);

    return personRepository
        .findById(id)
        .map(
            existingPerson -> {
              Optional.ofNullable(personEntity.getFirstName())
                  .ifPresent(existingPerson::setFirstName);
              Optional.ofNullable(personEntity.getLastName())
                  .ifPresent(existingPerson::setLastName);
              return personRepository.save(existingPerson);
            })
        .orElseThrow(() -> new RuntimeException("Person for partial update does not exist"));
  }

  @Override
  public void delete(Long id) {
    personRepository.deleteById(id);
  }
}
