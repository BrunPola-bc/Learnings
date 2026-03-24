package com.brunpola.cv_management.services.impl;

import com.brunpola.cv_management.domain.entities.ProjectEntity;
import com.brunpola.cv_management.repositories.ProjectRepository;
import com.brunpola.cv_management.services.ProjectService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;

/** TEST */
@Service
public class ProjectServiceImpl implements ProjectService {

  private final ProjectRepository projectRepository;

  /**
   * TEST
   *
   * @param projectRepository the repository for managing project entities
   */
  public ProjectServiceImpl(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  /**
   * TEST
   *
   * @param project project
   * @return created ProjectEntity
   */
  @Override
  public ProjectEntity createProject(ProjectEntity project) {
    return projectRepository.save(project);
  }

  /**
   * TEST
   *
   * @return found ProjectEntity list
   */
  @Override
  public List<ProjectEntity> findAll() {
    return StreamSupport.stream(projectRepository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }
}
