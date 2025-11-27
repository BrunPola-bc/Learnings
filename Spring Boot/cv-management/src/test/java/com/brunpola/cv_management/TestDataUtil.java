package com.brunpola.cv_management;

import com.brunpola.cv_management.domain.Person;
import com.brunpola.cv_management.domain.Project;
import com.brunpola.cv_management.domain.Skill;

public final class TestDataUtil {
  private TestDataUtil() {}

  public static Person createTestPersonA() {
    return Person.builder().firstName("Aruno").lastName("Pola").build();
  }

  public static Person createTestPersonB() {
    return Person.builder().firstName("Bruno").lastName("Polb").build();
  }

  public static Person createTestPersonC() {
    return Person.builder().firstName("Cruno").lastName("Polc").build();
  }

  public static Skill createTestSkill() {
    return Skill.builder().skillName("Mockito").build();
  }

  public static Project createTestProject() {
    return Project.builder().projectName("Mockito Testing").build();
  }
}
