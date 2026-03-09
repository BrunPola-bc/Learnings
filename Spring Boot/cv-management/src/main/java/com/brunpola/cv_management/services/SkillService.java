package com.brunpola.cv_management.services;

import com.brunpola.cv_management.domain.entities.SkillEntity;
import java.util.List;

public interface SkillService {

  SkillEntity createSkill(SkillEntity skill);

  List<SkillEntity> findAll();
}
