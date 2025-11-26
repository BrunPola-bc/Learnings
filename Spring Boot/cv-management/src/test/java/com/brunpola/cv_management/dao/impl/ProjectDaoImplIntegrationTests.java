package com.brunpola.cv_management.dao.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunpola.cv_management.TestDataUtil;
import com.brunpola.cv_management.domain.Project;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProjectDaoImplIntegrationTests {

  private final ProjectDaoImpl underTest;

  @Autowired
  public ProjectDaoImplIntegrationTests(ProjectDaoImpl underTest) {
    this.underTest = underTest;
  }

  @Test
  public void testThatProjectCanBeCreatedAndRecalled() {
    Project project = TestDataUtil.createTestProject();

    project = underTest.create(project);
    Optional<Project> result = underTest.findOne(project.getId());

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(project);
  }
}
