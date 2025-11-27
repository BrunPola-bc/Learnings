package com.brunpola.cv_management.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunpola.cv_management.TestDataUtil;
import com.brunpola.cv_management.domain.Project;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProjectRepositoryIntegrationTests {

  private final ProjectRepository underTest;

  @Autowired
  public ProjectRepositoryIntegrationTests(ProjectRepository underTest) {
    this.underTest = underTest;
  }

  @Test
  public void testThatProjectCanBeCreatedAndRecalled() {
    Project project = TestDataUtil.createTestProject();

    project = underTest.save(project);
    Optional<Project> result = underTest.findById(project.getId());

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(project);
  }

  @Test
  public void testThatMultipleProjectsCanBeRecalled() {
    Project project = TestDataUtil.createTestProject();
    project = underTest.save(project);

    // Already in DB
    Project project2 = Project.builder().id(2L).projectName("Database Migration").build();

    Iterable<Project> result = underTest.findAll();
    assertThat(result).contains(project, project2);
  }

  @Test
  public void testThatProjectCanBeUpdated() {
    Project project = TestDataUtil.createTestProject();
    project = underTest.save(project);
    project.setProjectName("NEW NAME");
    project = underTest.save(project);

    Optional<Project> result = underTest.findById(project.getId());
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(project);
  }

  @Test
  public void testThatProjectCanBeDeleted() {
    Project project1 = TestDataUtil.createTestProject();
    underTest.save(project1);

    Optional<Project> project2Optional = underTest.findById(2L);
    assertThat(project2Optional).isPresent();
    Project project2 = project2Optional.get();

    underTest.deleteById(project1.getId());
    underTest.delete(project2);

    Optional<Project> resultA = underTest.findById(project1.getId());
    Optional<Project> resultM = underTest.findById(project2.getId());

    assertThat(resultA).isEmpty();
    assertThat(resultM).isEmpty();
  }
}
