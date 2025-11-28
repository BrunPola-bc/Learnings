package com.brunpola.cv_management.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunpola.cv_management.TestDataUtil;
import com.brunpola.cv_management.domain.Person;
import com.brunpola.cv_management.domain.Project;
import com.brunpola.cv_management.domain.Skill;
import com.brunpola.cv_management.domain.join.PersonProject;
import com.brunpola.cv_management.domain.join.PersonProjectId;
import com.brunpola.cv_management.domain.join.PersonSkill;
import com.brunpola.cv_management.domain.join.PersonSkillId;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
// @ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PersonRepositoryIntegrationTests {

  private final PersonRepository underTest;

  private final SkillRepository skillRepository;
  private final ProjectRepository projectRepository;
  private final PersonSkillRepository personSkillRepository;
  private final PersonProjectRepository personProjectRepository;

  @Autowired
  public PersonRepositoryIntegrationTests(
      PersonRepository underTest,
      SkillRepository skillRepository,
      ProjectRepository projectRepository,
      PersonSkillRepository personSkillRepository,
      PersonProjectRepository personProjectRepository) {
    this.underTest = underTest;
    this.skillRepository = skillRepository;
    this.projectRepository = projectRepository;
    this.personSkillRepository = personSkillRepository;
    this.personProjectRepository = personProjectRepository;
  }

  @Test
  public void testThatPersonCanBeCreatedAndRecalled() {
    Person person = TestDataUtil.createTestPersonB();

    person = underTest.save(person);
    Optional<Person> result = underTest.findById(person.getId());

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(person);
  }

  @Test
  public void testThatMultiplePeopleCanBeCreatedAndRecalled() {
    Person personA = TestDataUtil.createTestPersonA();
    personA = underTest.save(personA);
    Person personB = TestDataUtil.createTestPersonB();
    personB = underTest.save(personB);
    Person personC = TestDataUtil.createTestPersonC();
    personC = underTest.save(personC);

    // Already in DB
    Person personM = Person.builder().id(7L).firstName("Michael").lastName("Scott").build();

    Optional<Person> personD = underTest.findById(8L);

    Iterable<Person> result = underTest.findAll();
    assertThat(result).contains(personA, personB, personC, personM);
    assertThat(personA).isEqualTo(personD.get());
  }

  @Test
  public void testThatPersonCanBeUpdated() {
    Person personA = TestDataUtil.createTestPersonA();
    personA = underTest.save(personA);
    personA.setFirstName("NEW NAME");
    personA = underTest.save(personA);

    Optional<Person> result = underTest.findById(personA.getId());
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(personA);
  }

  @Test
  public void testThatPersonCanBeDeleted() {
    Person personA = TestDataUtil.createTestPersonA();
    underTest.save(personA);

    Optional<Person> personMOptional = underTest.findById(7L);
    assertThat(personMOptional).isPresent();
    Person personM = personMOptional.get();

    underTest.deleteById(personA.getId());
    underTest.delete(personM);

    Optional<Person> resultA = underTest.findById(personA.getId());
    Optional<Person> resultM = underTest.findById(personM.getId());

    assertThat(resultA).isEmpty();
    assertThat(resultM).isEmpty();
  }

  @Test
  @Transactional
  public void testToFetchSkills() {
    Optional<Person> personOpt = underTest.findById(1L);
    assertThat(personOpt).isPresent();
    Person person = personOpt.get();

    Set<PersonSkill> skills = person.getSkills();
    assertThat(skills).isNotEmpty();
  }

  @Test
  @Transactional
  public void testThatDeletePersonCascadeDeletes() {
    Person person = underTest.findById(1L).orElseThrow();

    PersonSkill firsPersonSkill = person.getSkills().stream().findFirst().orElseThrow();
    Skill skill = firsPersonSkill.getSkill();

    PersonProject firsPersonProject = person.getProjects().stream().findFirst().orElseThrow();
    Project project = firsPersonProject.getProject();

    underTest.delete(person);

    // Person deleted
    assertThat(underTest.findById(1L)).isEmpty();

    boolean personSkillExists =
        personSkillRepository.existsById(new PersonSkillId(person.getId(), skill.getId()));
    boolean personProjectExists =
        personProjectRepository.existsById(new PersonProjectId(person.getId(), project.getId()));

    // Junction tables entries deleted
    assertThat(personSkillExists).isFalse();
    assertThat(personProjectExists).isFalse();

    // Skills and projects not deleted
    assertThat(skillRepository.findById(skill.getId())).isPresent();
    assertThat(projectRepository.findById(project.getId())).isPresent();
  }

  @Test
  public void testForLastNameContains() {
    Optional<Person> personMOptional = underTest.findById(7L);
    assertThat(personMOptional).isPresent();
    Person personM = personMOptional.get();

    // Person personA = TestDataUtil.createTestPersonA();
    // underTest.save(personA);
    Person personA = underTest.findById(1L).orElseThrow();

    Iterable<Person> peopleWith = underTest.lastNameContains("tt");

    assertThat(peopleWith).contains(personM);
    assertThat(peopleWith).doesNotContain(personA);
  }

  @Test
  public void testHQL() {
    Optional<Person> personMOptional = underTest.findById(7L);
    assertThat(personMOptional).isPresent();
    Person personM = personMOptional.get();

    Iterable<Person> peopleWith = underTest.testMethod("tt");

    assertThat(peopleWith).doesNotContain(personM);
  }
}
