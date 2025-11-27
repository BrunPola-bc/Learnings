package com.brunpola.cv_management.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunpola.cv_management.TestDataUtil;
import com.brunpola.cv_management.domain.Person;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
// @ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PersonRepositoryIntegrationTests {

  private final PersonRepository underTest;

  @Autowired
  public PersonRepositoryIntegrationTests(PersonRepository underTest) {
    this.underTest = underTest;
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
}
