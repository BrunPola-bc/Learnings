package com.brunpola.cv_management.domain.join;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Composite key for {@link PersonProject}.
 *
 * <p>Represents the primary key of the join table, consisting of:
 *
 * <ul>
 *   <li>{@code personId} – identifier of the person
 *   <li>{@code projectId} – identifier of the project
 * </ul>
 *
 * <p>This class must be {@link Serializable} as required by JPA for embedded primary keys.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonProjectId implements Serializable {

  /** Identifier of the person. */
  private Long personId;

  /** Identifier of the project. */
  private Long projectId;
}
