package com.brunpola.cv_management.dao.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunpola.cv_management.TestDataUtil;
import com.brunpola.cv_management.domain.Skill;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SkillDaoImplIntegrationTests {

  private final SkillDaoImpl underTest;

  @Autowired
  public SkillDaoImplIntegrationTests(SkillDaoImpl underTest) {
    this.underTest = underTest;
  }

  @Test
  public void testThatSkillCanBeCreatedAndRecalled() {
    Skill skill = TestDataUtil.createTestSkill();

    skill = underTest.create(skill);
    Optional<Skill> result = underTest.findOne(skill.getId());

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(skill);
  }

  @Test
  public void testThatMultipleSkillsCanBeRecalled() {
    Skill skill = TestDataUtil.createTestSkill();
    skill = underTest.create(skill);

    // Already in DB
    Skill skill2 = Skill.builder().id(2L).skillName("SQL").build();

    List<Skill> result = underTest.find();
    assertThat(result).contains(skill, skill2);
  }
}
