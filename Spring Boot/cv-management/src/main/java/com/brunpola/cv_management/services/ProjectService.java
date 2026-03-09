package com.brunpola.cv_management.services;

import com.brunpola.cv_management.domain.entities.ProjectEntity;
import java.util.List;

public interface ProjectService {

  ProjectEntity createProject(ProjectEntity project);

  List<ProjectEntity> findAll();
}
