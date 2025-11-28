package com.brunpola.cv_management.domain;

import com.brunpola.cv_management.domain.join.PersonProject;
import com.brunpola.cv_management.domain.join.ProjectSkill;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(exclude = {"people", "skills"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Projects")
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long id;

  @Column(name = "ProjectName")
  private String projectName;

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<PersonProject> people = new HashSet<>();

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<ProjectSkill> skills = new HashSet<>();
}
