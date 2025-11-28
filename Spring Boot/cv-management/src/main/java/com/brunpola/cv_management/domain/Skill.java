package com.brunpola.cv_management.domain;

import com.brunpola.cv_management.domain.join.PersonSkill;
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
@EqualsAndHashCode(exclude = {"people", "projects"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Skills")
public class Skill {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long id;

  @Column(name = "SkillName")
  private String skillName;

  // Many-to-many via join entity PersonSkill
  @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<PersonSkill> people = new HashSet<>();

  // Many-to-many via join entity ProjectSkill
  @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<ProjectSkill> projects = new HashSet<>();
}
