package com.brunpola.cv_management.repositories;

import com.brunpola.cv_management.domain.entities.PersonEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository
    extends CrudRepository<PersonEntity, Long>, PagingAndSortingRepository<PersonEntity, Long> {

  Iterable<PersonEntity> lastNameContains(String part);

  @Query("SELECT p from PersonEntity p where p.lastName not like concat( '%', ?1, '%') ")
  Iterable<PersonEntity> testMethod(String part);
}
