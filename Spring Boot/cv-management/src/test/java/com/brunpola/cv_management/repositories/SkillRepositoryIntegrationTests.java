package com.brunpola.cv_management.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunpola.cv_management.TestDataUtil;
import com.brunpola.cv_management.domain.Skill;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SkillRepositoryIntegrationTests {

  private final SkillRepository underTest;

  @Autowired
  public SkillRepositoryIntegrationTests(SkillRepository underTest) {
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

    Optional<Skill> resultA = underTest.findById(skill1.getId());
    Optional<Skill> resultM = underTest.findById(skill2.getId());

    assertThat(resultA).isEmpty();
    assertThat(resultM).isEmpty();
  }
}
