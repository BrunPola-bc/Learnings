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
  SkillEntity save(SkillEntity skill);

  /**
   * TEST
   *
   * @return list of SkillEntity
   */
  List<SkillEntity> findAll();

  /**
   * TEST
   *
   * @param id id of skill
   * @return skill entity
   */
  SkillEntity findOne(long id);

  void delete(Long id);

  boolean isExists(Long id);
}
