package com.brunpola.cv_management.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunpola.cv_management.TestDataUtil;
import com.brunpola.cv_management.domain.Person;
import com.brunpola.cv_management.domain.Project;
import com.brunpola.cv_management.domain.Skill;
import com.brunpola.cv_management.domain.join.PersonProject;
import com.brunpola.cv_management.domain.join.PersonProjectId;
import com.brunpola.cv_management.domain.join.ProjectSkill;
import com.brunpola.cv_management.domain.join.ProjectSkillId;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProjectRepositoryIntegrationTests {

  private final ProjectRepository underTest;

  private final PersonRepository personRepository;
  private final SkillRepository skillRepository;
  private final PersonProjectRepository personProjectRepository;
  private final ProjectSkillRepository projectSkillRepository;

  @Autowired
  public ProjectRepositoryIntegrationTests(
      ProjectRepository underTest,
      PersonRepository personRepository,
      SkillRepository skillRepository,
      PersonProjectRepository personProjectRepository,
      ProjectSkillRepository projectSkillRepository) {
    this.underTest = underTest;
    this.personRepository = personRepository;
    this.skillRepository = skillRepository;
    this.personProjectRepository = personProjectRepository;
    this.projectSkillRepository = projectSkillRepository;
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

    Optional<Project> result1 = underTest.findById(project1.getId());
    Optional<Project> result2 = underTest.findById(project2.getId());

    assertThat(result1).isEmpty();
    assertThat(result2).isEmpty();
  }

  @Test
  @Transactional
  public void testThatDeleteProjectCascadeDeletes() {
    Project project = underTest.findById(1L).orElseThrow();

    PersonProject firstPersonProject = project.getPeople().stream().findFirst().orElseThrow();
    Person person = firstPersonProject.getPerson();

    ProjectSkill firstProjectSkill = project.getSkills().stream().findFirst().orElseThrow();
    Skill skill = firstProjectSkill.getSkill();

    underTest.delete(project);

    // Project deleted
    assertThat(underTest.findById(1L)).isEmpty();

    boolean personProjectExists =
        personProjectRepository.existsById(new PersonProjectId(person.getId(), project.getId()));
    boolean projectSkillExists =
        projectSkillRepository.existsById(new ProjectSkillId(project.getId(), skill.getId()));

    // Junction table entries deleted
    assertThat(personProjectExists).isFalse();
    assertThat(projectSkillExists).isFalse();

    // Persons and Projects not deleted
    assertThat(personRepository.findById(person.getId())).isPresent();
    assertThat(skillRepository.findById(skill.getId())).isPresent();
  }
}
