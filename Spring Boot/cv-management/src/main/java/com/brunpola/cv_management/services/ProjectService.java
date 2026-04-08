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
  ProjectEntity save(ProjectEntity project);

  /**
   * TEST
   *
   * @return list of all projects
   */
  List<ProjectEntity> findAll();

  /**
   * TEST
   *
   * @param id id of project
   * @return the project entitiy
   */
  ProjectEntity findOne(long id);

  void delete(Long id);

  boolean isExists(Long id);
}
