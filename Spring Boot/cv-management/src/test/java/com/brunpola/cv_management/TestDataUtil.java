package com.brunpola.cv_management;

import com.brunpola.cv_management.domain.dto.PersonDto;
import com.brunpola.cv_management.domain.entities.PersonEntity;
import com.brunpola.cv_management.domain.entities.ProjectEntity;
import com.brunpola.cv_management.domain.entities.SkillEntity;

public final class TestDataUtil {
  private TestDataUtil() {}

  public static PersonEntity createTestPersonA() {
    return PersonEntity.builder().firstName("Aruno").lastName("Pola").build();
  }

  public static PersonDto createTestPersonDtoA() {
    return PersonDto.builder().firstName("Aruno").lastName("Pola").build();
  }

  public static PersonEntity createTestPersonB() {
    return PersonEntity.builder().firstName("Bruno").lastName("Polb").build();
  }

  public static PersonEntity createTestPersonC() {
    return PersonEntity.builder().firstName("Cruno").lastName("Polc").build();
  }

  public static SkillEntity createTestSkill() {
    return SkillEntity.builder().skillName("Mockito").build();
  }

  public static ProjectEntity createTestProject() {
    return ProjectEntity.builder().projectName("Mockito Testing").build();
  }
}
