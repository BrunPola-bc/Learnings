package com.brunpola.cv_management.services.impl;

import com.brunpola.cv_management.domain.entities.ProjectEntity;
import com.brunpola.cv_management.repositories.ProjectRepository;
import com.brunpola.cv_management.services.ProjectService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
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

  @Override
  public List<ProjectEntity> findAll() {
    return StreamSupport.stream(projectRepository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }
}
