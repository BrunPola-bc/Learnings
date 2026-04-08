package com.brunpola.cv_management.domain.join;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunpola.cv_management.domain.entities.ProjectEntity;
import com.brunpola.cv_management.domain.entities.SkillEntity;
import org.junit.jupiter.api.Test;

class ProjectSkillTests {

  @Test
  void testConstructorSetsFieldsCorrectly() {
    ProjectEntity project = new ProjectEntity();
    project.setId(1L);

    SkillEntity skill = new SkillEntity();
    skill.setId(2L);

    ProjectSkill projectSkill = new ProjectSkill(project, skill);

    assertThat(projectSkill.getProject()).isEqualTo(project);
    assertThat(projectSkill.getSkill()).isEqualTo(skill);
    assertThat(projectSkill.getId()).isNotNull();
    assertThat(projectSkill.getId().getProjectId()).isEqualTo(1L);
    assertThat(projectSkill.getId().getSkillId()).isEqualTo(2L);
  }
}
