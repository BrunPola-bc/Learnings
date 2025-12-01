package com.brunpola.cv_management.domain.join;

import com.brunpola.cv_management.domain.entities.ProjectEntity;
import com.brunpola.cv_management.domain.entities.SkillEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "ProjectSkills")
public class ProjectSkill {

  @EmbeddedId private ProjectSkillId id;

  @ManyToOne
  @MapsId("projectId")
  @JoinColumn(name = "ProjectID")
  private ProjectEntity project;

  @ManyToOne
  @MapsId("skillId")
  @JoinColumn(name = "SkillID")
  private SkillEntity skill;

  public ProjectSkill(ProjectEntity project, SkillEntity skill) {
    this.project = project;
    this.skill = skill;
    this.id = new ProjectSkillId(project.getId(), skill.getId());
  }
}
