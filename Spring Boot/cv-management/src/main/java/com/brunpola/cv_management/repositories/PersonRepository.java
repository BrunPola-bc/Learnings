package com.brunpola.cv_management.repositories;

import com.brunpola.cv_management.domain.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

  Iterable<Person> lastNameContains(String part);

  @Query("SELECT p from Person p where p.lastName not like concat( '%', ?1, '%') ")
  Iterable<Person> testMethod(String part);
}
