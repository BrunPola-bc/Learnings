package com.brunpola.cv_management.dao;

import com.brunpola.cv_management.domain.Skill;
import java.util.List;
import java.util.Optional;

public interface SkillDao {

  Skill create(Skill skill);

  Optional<Skill> findOne(long skillId);

  List<Skill> find();
}
