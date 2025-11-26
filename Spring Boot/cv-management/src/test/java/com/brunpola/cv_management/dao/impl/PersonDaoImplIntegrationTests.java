package com.brunpola.cv_management.dao.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunpola.cv_management.TestDataUtil;
import com.brunpola.cv_management.domain.Person;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
// @ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PersonDaoImplIntegrationTests {

  private final PersonDaoImpl underTest;

  @Autowired
  public PersonDaoImplIntegrationTests(PersonDaoImpl underTest) {
    this.underTest = underTest;
  }

  @Test
  public void testThatPersonCanBeCreatedAndRecalled() {
    Person person = TestDataUtil.createTestPersonB();

    person = underTest.create(person);
    Optional<Person> result = underTest.findOne(person.getId());

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(person);
  }

  @Test
  public void testThatMultiplePeopleCanBeCreatedAndRecalled() {
    Person personA = TestDataUtil.createTestPersonA();
    personA = underTest.create(personA);
    Person personB = TestDataUtil.createTestPersonB();
    personB = underTest.create(personB);
    Person personC = TestDataUtil.createTestPersonC();
    personC = underTest.create(personC);

    // Already in DB
    Person personM = Person.builder().id(7L).firstName("Michael").lastName("Scott").build();

    // Person personD = underTest.findOne(8L).get();

    List<Person> result = underTest.find();
    assertThat(result).contains(personA, personB, personC, personM);
    // assertThat(personA).isEqualTo(personD);
  }
}
