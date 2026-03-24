package com.brunpola.cv_management.repositories;

import com.brunpola.cv_management.domain.entities.SkillEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** CRUD Repository for {@link SkillEntity} entities */
@Repository
public interface SkillRepository extends CrudRepository<SkillEntity, Long> {}
