package com.brunpola.cv_management.repositories;

import com.brunpola.cv_management.domain.join.ProjectSkill;
import com.brunpola.cv_management.domain.join.ProjectSkillId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectSkillRepository extends JpaRepository<ProjectSkill, ProjectSkillId> {}
