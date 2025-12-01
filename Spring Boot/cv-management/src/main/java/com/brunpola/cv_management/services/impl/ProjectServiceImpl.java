package com.brunpola.cv_management.services.impl;

import com.brunpola.cv_management.domain.entities.ProjectEntity;
import com.brunpola.cv_management.repositories.ProjectRepository;
import com.brunpola.cv_management.services.ProjectService;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {

  private final ProjectRepository projectRepository;

  public ProjectServiceImpl(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  @Override
  public ProjectEntity createProject(ProjectEntity project) {
    return projectRepository.save(project);
  }
}
