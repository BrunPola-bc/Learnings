package com.brunpola.cv_management.repositories;

import com.brunpola.cv_management.domain.entities.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link PersonEntity}. Provides CRUD operations and additional query
 * methods for Person entities.
 */
@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

  /**
   * Finds all persons whose last name contains the given substring.
   *
   * @param part the substring to search for in last names
   * @return iterable of matching {@link PersonEntity} objects
   */
  Iterable<PersonEntity> lastNameContains(String part);

  /**
   * Finds all persons whose last name does not contain the given substring. Demonstrates a custom
   * JPQL query using the {@link Query} annotation.
   *
   * @param part the substring to exclude from last names
   * @return iterable of {@link PersonEntity} objects not containing the substring
   */
  @Query("SELECT p from PersonEntity p where p.lastName not like concat( '%', ?1, '%') ")
  Iterable<PersonEntity> testMethod(String part);
}
