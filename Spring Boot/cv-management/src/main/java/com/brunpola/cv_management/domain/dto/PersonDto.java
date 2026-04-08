package com.brunpola.cv_management.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for a Person.
 *
 * <p>Represents basic information about a person, used to transfer data between the service layer
 * and controllers or external clients.
 *
 * <p>This class uses Lombok annotations to generate:
 *
 * <ul>
 *   <li>Getters and setters (@Data)
 *   <li>No-args and all-args constructors (@NoArgsConstructor, @AllArgsConstructor)
 *   <li>A builder for convenient object creation (@Builder)
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonDto {

  /** Unique identifier of the person. */
  private Long id;

  /** Person's first name. */
  @NotNull private String firstName;

  /** Person's last name. */
  @NotNull private String lastName;
}
