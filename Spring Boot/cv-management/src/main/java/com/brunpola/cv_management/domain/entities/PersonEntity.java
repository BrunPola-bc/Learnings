package com.brunpola.cv_management.domain.entities;

import com.brunpola.cv_management.domain.join.PersonProject;
import com.brunpola.cv_management.domain.join.PersonSkill;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(exclude = {"skills", "projects"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "People")
public class PersonEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // This should work for my 'week1database'
  @Column(name = "ID")
  private Long id;

  @Column(name = "FirstName")
  @NotNull
  private String firstName;

  @Column(name = "LastName")
  @NotNull
  private String lastName;

  @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<PersonSkill> skills = new HashSet<>();

  @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<PersonProject> projects = new HashSet<>();
}
