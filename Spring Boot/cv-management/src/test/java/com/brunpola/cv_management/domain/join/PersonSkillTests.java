package com.brunpola.cv_management.domain.join;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunpola.cv_management.domain.entities.PersonEntity;
import com.brunpola.cv_management.domain.entities.SkillEntity;
import org.junit.jupiter.api.Test;

class PersonSkillTests {

  @Test
  void testConstructorSetsFieldsCorrectly() {
    PersonEntity person = new PersonEntity();
    person.setId(1L);

    SkillEntity skill = new SkillEntity();
    skill.setId(2L);

    PersonSkill personSkill = new PersonSkill(person, skill);

    assertThat(personSkill.getPerson()).isEqualTo(person);
    assertThat(personSkill.getSkill()).isEqualTo(skill);
    assertThat(personSkill.getId()).isNotNull();
    assertThat(personSkill.getId().getPersonId()).isEqualTo(1L);
    assertThat(personSkill.getId().getSkillId()).isEqualTo(2L);
  }
}
