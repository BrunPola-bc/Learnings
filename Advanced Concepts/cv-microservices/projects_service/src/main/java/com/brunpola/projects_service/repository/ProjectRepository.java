package com.brunpola.projects_service.repository;

import com.brunpola.projects_service.domain.entity.ProjectEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

  List<ProjectEntity> findBySkillIdsContaining(Long skillId);
}
