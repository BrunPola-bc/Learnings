package com.brunpola.cv_management.domain.entities;

import com.brunpola.cv_management.domain.join.PersonProject;
import com.brunpola.cv_management.domain.join.PersonSkill;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * JPA entity representing a person in the CV management system.
 *
 * <p>Maps to the {@code People} table in the database and contains basic personal information along
 * with relationships to skills and projects.
 *
 * <p>This entity maintains one-to-many relationships with {@link PersonSkill} and {@link
 * PersonProject}, representing the person's associated skills and projects.
 *
 * <p>Lombok is used to generate boilerplate code such as getters, setters, constructors, and
 * builder methods.
 */
@Data
@EqualsAndHashCode(exclude = {"skills", "projects"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "People")
public class PersonEntity {

  /**
   * Unique identifier of the person.
   *
   * <p>Auto-generated using IDENTITY strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // This should work for my 'week1database'
  @Column(name = "ID")
  private Long id;

  /**
   * First name of the person.
   *
   * <p>Cannot be null.
   */
  @Column(name = "FirstName")
  @NotNull
  private String firstName;

  /**
   * Last name of the person.
   *
   * <p>Cannot be null.
   */
  @Column(name = "LastName")
  @NotNull
  private String lastName;

  /**
   * Set of skills associated with the person.
   *
   * <p>Mapped by the {@code person} field in {@link PersonSkill}. Uses cascading operations and
   * orphan removal, meaning:
   *
   * <ul>
   *   <li>Persist/update operations propagate to related skills
   *   <li>Removing a skill from this set deletes it from the database
   * </ul>
   */
  @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<PersonSkill> skills = new HashSet<>();

  /**
   * Set of projects associated with the person.
   *
   * <p>Mapped by the {@code person} field in {@link PersonProject}. Uses cascading operations and
   * orphan removal.
   */
  @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<PersonProject> projects = new HashSet<>();
}
