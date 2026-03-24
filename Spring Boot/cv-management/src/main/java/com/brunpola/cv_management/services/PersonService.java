package com.brunpola.cv_management.services;

import com.brunpola.cv_management.domain.entities.PersonEntity;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

/**
 * Service interface defining operations for managing {@link PersonEntity} objects.
 *
 * <p>Includes CRUD operations, search, and partial updates.
 */
public interface PersonService {

  /**
   * Saves a new person entity to the repository.
   *
   * @param person the {@link PersonEntity} to save
   * @return the saved {@link PersonEntity} with generated ID
   */
  @Validated
  PersonEntity save(@Valid PersonEntity person);

  /**
   * Fully updates an existing person entity.
   *
   * @param person the {@link PersonEntity} with updated fields (must include ID)
   * @return the updated {@link PersonEntity}
   * @throws com.brunpola.cv_management.exceptions.person.PersonNotFoundException if the person does
   *     not exist
   */
  PersonEntity update(PersonEntity person);

  /**
   * Retrieves all person entities.
   *
   * @return a {@link List} of all {@link PersonEntity} objects
   */
  List<PersonEntity> findAll();

  /**
   * Retrieves all person entities in a paginated manner.
   *
   * @param pageable the {@link Pageable} pagination and sorting information
   * @return a {@link Page} of {@link PersonEntity} objects
   */
  Page<PersonEntity> findAll(Pageable pageable);

  /**
   * Retrieves a single person by its ID.
   *
   * @param id the ID of the person
   * @return the {@link PersonEntity} with the given ID
   * @throws com.brunpola.cv_management.exceptions.person.PersonNotFoundException if the person does
   *     not exist
   */
  PersonEntity findOne(Long id);

  /**
   * Checks if a person with the given ID exists.
   *
   * @param id the ID to check
   * @return true if the person exists, false otherwise
   */
  boolean isExists(Long id);

  /**
   * Partially updates an existing person entity. Only non-null fields in {@code personEntity} are
   * updated.
   *
   * @param id the ID of the person to update
   * @param personEntity the {@link PersonEntity} with fields to update
   * @return the updated {@link PersonEntity}
   * @throws com.brunpola.cv_management.exceptions.person.PersonNotFoundException if the person does
   *     not exist
   */
  PersonEntity partialUpdate(Long id, PersonEntity personEntity);

  /**
   * Deletes a person entity by ID.
   *
   * @param id the ID of the person to delete
   * @throws com.brunpola.cv_management.exceptions.person.PersonNotFoundException if the person does
   *     not exist
   */
  void delete(Long id);

  /**
   * Searches for person entities matching the non-null fields of the given probe. Partial matches
   * on first and last name are case-insensitive.
   *
   * @param personEntity a {@link PersonEntity} with search criteria
   * @return a {@link List} of matching {@link PersonEntity} objects
   */
  List<PersonEntity> search(PersonEntity personEntity);
}
