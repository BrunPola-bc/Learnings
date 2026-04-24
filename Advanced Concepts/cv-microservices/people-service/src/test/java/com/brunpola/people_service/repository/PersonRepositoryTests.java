package com.brunpola.people_service.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.brunpola.people_service.TestDataUtil;
import com.brunpola.people_service.domain.entity.PersonEntity;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class PersonRepositoryTests {

  @Autowired private PersonRepository personRepository;

  @Test
  void findByProjectIdsContaining_shouldReturnPeopleWithGivenProjectId() {

    // given
    List<PersonEntity> people = TestDataUtil.samplePeopleEntities(false, 3);
    people.get(0).setProjectIds(List.of(1L, 2L));
    people.get(1).setProjectIds(List.of(1L, 3L));
    people.get(2).setProjectIds(List.of(2L, 3L));

    personRepository.saveAll(people);

    // when
    List<PersonEntity> result = personRepository.findByProjectIdsContaining(2L);

    // then
    assertEquals(2, result.size());
    assertEquals(people.get(0), result.get(0));
    assertEquals(people.get(2), result.get(1));
  }

  @Test
  void findBySkillIdsContaining_shouldReturnPeopleWithGivenSkillId() {
    // given
    List<PersonEntity> people = TestDataUtil.samplePeopleEntities(false, 3);
    people.get(0).setSkillIds(List.of(1L, 2L));
    people.get(1).setSkillIds(List.of(1L, 3L));
    people.get(2).setSkillIds(List.of(2L, 3L));

    personRepository.saveAll(people); // This updates ids in people list

    // when
    List<PersonEntity> result = personRepository.findBySkillIdsContaining(2L);

    // then
    assertEquals(2, result.size());

    assertTrue(result.contains(people.get(0)));
    assertTrue(result.contains(people.get(2)));
  }
}
