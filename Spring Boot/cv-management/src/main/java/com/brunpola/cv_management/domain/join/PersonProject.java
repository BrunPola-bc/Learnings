package com.brunpola.cv_management.domain.join;

import com.brunpola.cv_management.domain.entities.PersonEntity;
import com.brunpola.cv_management.domain.entities.ProjectEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Join entity representing the many-to-many relationship between {@link PersonEntity} and {@link
 * ProjectEntity}.
 *
 * <p>This entity maps to the {@code PersonProjects} join table, which links persons and projects
 * using a composite primary key.
 *
 * <p>The relationship is implemented using an embedded ID ({@link PersonProjectId}) and
 * {@code @MapsId} to synchronize the foreign keys with the composite key fields.
 *
 * <p>This approach is used instead of {@code @ManyToMany} to allow future extension of the
 * relationship with additional attributes.
 */
@Entity
@NoArgsConstructor
@Data
@Table(name = "PersonProjects")
public class PersonProject {

  /** Composite primary key consisting of person ID and project ID. */
  @EmbeddedId private PersonProjectId id;

  /**
   * Reference to the associated person.
   *
   * <p>Mapped to {@code PersonID} and linked to the {@code personId} field in the composite key.
   */
  @ManyToOne
  @MapsId("personId")
  @JoinColumn(name = "PersonID")
  private PersonEntity person;

  /**
   * Reference to the associated project.
   *
   * <p>Mapped to {@code ProjectID} and linked to the {@code projectId} field in the composite key.
   */
  @ManyToOne
  @MapsId("projectId")
  @JoinColumn(name = "ProjectID")
  private ProjectEntity project;

  /**
   * Convenience constructor for creating a relationship between a person and a project.
   *
   * @param person the person entity
   * @param project the project entity
   */
  public PersonProject(PersonEntity person, ProjectEntity project) {
    this.person = person;
    this.project = project;
    this.id = new PersonProjectId(person.getId(), project.getId());
  }
}
