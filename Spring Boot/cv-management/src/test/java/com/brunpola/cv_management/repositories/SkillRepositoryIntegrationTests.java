package com.brunpola.cv_management.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunpola.cv_management.TestDataUtil;
import com.brunpola.cv_management.domain.Person;
import com.brunpola.cv_management.domain.Project;
import com.brunpola.cv_management.domain.Skill;
import com.brunpola.cv_management.domain.join.PersonSkill;
import com.brunpola.cv_management.domain.join.PersonSkillId;
import com.brunpola.cv_management.domain.join.ProjectSkill;
import com.brunpola.cv_management.domain.join.ProjectSkillId;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SkillRepositoryIntegrationTests {

  private final SkillRepository underTest;

  private final PersonRepository personRepository;
  private final ProjectRepository projectRepository;
  private final PersonSkillRepository personSkillRepository;
  private final ProjectSkillRepository projectSkillRepository;

  @Autowired
  public SkillRepositoryIntegrationTests(
      PersonRepository personRepository,
      PersonSkillRepository personSkillRepository,
      ProjectRepository projectRepository,
      ProjectSkillRepository projectSkillRepository,
      SkillRepository underTest) {
    this.personRepository = personRepository;
    this.personSkillRepository = personSkillRepository;
    this.projectRepository = projectRepository;
    this.projectSkillRepository = projectSkillRepository;
    this.underTest = underTest;
  }

  @Test
  public void testThatSkillCanBeCreatedAndRecalled() {
    Skill skill = TestDataUtil.createTestSkill();

    skill = underTest.save(skill);
    Optional<Skill> result = underTest.findById(skill.getId());

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(skill);
  }

  @Test
  public void testThatMultipleSkillsCanBeRecalled() {
    Skill skill = TestDataUtil.createTestSkill();
    skill = underTest.save(skill);

    // Already in DB
    Skill skill2 = Skill.builder().id(2L).skillName("SQL").build();

    Iterable<Skill> result = underTest.findAll();
    assertThat(result).contains(skill, skill2);
  }

  @Test
  public void testThatSkillCanBeUpdated() {
    Skill skill = TestDataUtil.createTestSkill();
    skill = underTest.save(skill);
    skill.setSkillName("NEW NAME");
    skill = underTest.save(skill);

    Optional<Skill> result = underTest.findById(skill.getId());
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(skill);
  }

  @Test
  public void testThatSkillCanBeDeleted() {
    Skill skill1 = TestDataUtil.createTestSkill();
    underTest.save(skill1);

    Optional<Skill> skill2Optional = underTest.findById(2L);
    assertThat(skill2Optional).isPresent();
    Skill skill2 = skill2Optional.get();

    underTest.deleteById(skill1.getId());
    underTest.delete(skill2);

    Optional<Skill> result1 = underTest.findById(skill1.getId());
    Optional<Skill> result2 = underTest.findById(skill2.getId());

    assertThat(result1).isEmpty();
    assertThat(result2).isEmpty();
  }

  @Test
  @Transactional
  public void testThatDeleteSkillCascadeDeletes() {
    Skill skill = underTest.findById(1L).orElseThrow();

    PersonSkill firstPersonSkill = skill.getPeople().stream().findFirst().orElseThrow();
    Person person = firstPersonSkill.getPerson();

    ProjectSkill firstProjectSkill = skill.getProjects().stream().findFirst().orElseThrow();
    Project project = firstProjectSkill.getProject();

    underTest.delete(skill);

    // Skill deleted
    assertThat(underTest.findById(1L)).isEmpty();

    boolean personSkillExists =
        personSkillRepository.existsById(new PersonSkillId(person.getId(), skill.getId()));
    boolean projectSkillExists =
        projectSkillRepository.existsById(new ProjectSkillId(project.getId(), skill.getId()));

    // Junction table entries deleted
    assertThat(personSkillExists).isFalse();
    assertThat(projectSkillExists).isFalse();

    // Persons and Projects not deleted
    assertThat(personRepository.findById(person.getId())).isPresent();
    assertThat(projectRepository.findById(project.getId())).isPresent();
  }
}
