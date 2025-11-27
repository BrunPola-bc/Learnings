package com.brunpola.cv_management.repositories;

import com.brunpola.cv_management.domain.Skill;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends CrudRepository<Skill, Long> {}
