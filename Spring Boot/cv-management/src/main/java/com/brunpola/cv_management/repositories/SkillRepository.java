package com.brunpola.cv_management.repositories;

import com.brunpola.cv_management.domain.entities.SkillEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends CrudRepository<SkillEntity, Long> {}
