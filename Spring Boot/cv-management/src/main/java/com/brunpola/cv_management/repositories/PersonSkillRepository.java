package com.brunpola.cv_management.repositories;

import com.brunpola.cv_management.domain.join.PersonSkill;
import com.brunpola.cv_management.domain.join.PersonSkillId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonSkillRepository extends JpaRepository<PersonSkill, PersonSkillId> {}
