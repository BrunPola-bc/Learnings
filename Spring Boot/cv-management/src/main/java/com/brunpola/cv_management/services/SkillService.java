package com.brunpola.cv_management.services;

import com.brunpola.cv_management.domain.entities.SkillEntity;
import java.util.List;

/** TEST */
public interface SkillService {
  /**
   * TEST
   *
   * @param skill skill
   * @return skillEntity
   */
  SkillEntity createSkill(SkillEntity skill);

  /**
   * TEST
   *
   * @return list of SkillEntity
   */
  List<SkillEntity> findAll();
}
