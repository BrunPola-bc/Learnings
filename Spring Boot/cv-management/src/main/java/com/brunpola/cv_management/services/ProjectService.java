package com.brunpola.cv_management.services;

import com.brunpola.cv_management.domain.entities.ProjectEntity;
import java.util.List;

/** TEST */
public interface ProjectService {
  /**
   * TEST
   *
   * @param project the project to create
   * @return the created project entity
   */
  ProjectEntity createProject(ProjectEntity project);

  /**
   * TEST
   *
   * @return list of all projects
   */
  List<ProjectEntity> findAll();
}
