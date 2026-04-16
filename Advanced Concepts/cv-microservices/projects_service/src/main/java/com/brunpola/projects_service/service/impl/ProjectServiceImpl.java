package com.brunpola.projects_service.service.impl;

import com.brunpola.projects_service.domain.dto.ProjectDto;
import com.brunpola.projects_service.domain.entity.ProjectEntity;
import com.brunpola.projects_service.exception.ProjectNotFoundException;
import com.brunpola.projects_service.mapper.Mapper;
import com.brunpola.projects_service.repository.ProjectRepository;
import com.brunpola.projects_service.service.ProjectService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {

  private final ProjectRepository projectRepository;
  private final Mapper<ProjectEntity, ProjectDto> projectMapper;

  public ProjectServiceImpl(
      ProjectRepository projectRepository, Mapper<ProjectEntity, ProjectDto> projectMapper) {
    this.projectRepository = projectRepository;
    this.projectMapper = projectMapper;
  }

  @Override
  public ProjectDto save(ProjectDto projectDto) {
    ProjectEntity projectEntity = projectMapper.mapFrom(projectDto);
    projectEntity = projectRepository.save(projectEntity);
    return projectMapper.mapTo(projectEntity);
  }

  @Override
  public ProjectDto update(ProjectDto projectDto) {
    if (!isExists(projectDto.getId())) {
      throw new ProjectNotFoundException(projectDto.getId());
    }

    ProjectEntity projectEntity = projectMapper.mapFrom(projectDto);
    projectEntity = projectRepository.save(projectEntity);
    return projectMapper.mapTo(projectEntity);
  }

  @Override
  public List<ProjectDto> findAll() {
    return projectRepository.findAll().stream().map(projectMapper::mapTo).toList();
  }

  @Override
  public ProjectDto findOne(Long id) {
    ProjectEntity projectEntity =
        projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));
    return projectMapper.mapTo(projectEntity);
  }

  @Override
  public boolean isExists(Long id) {
    return projectRepository.existsById(id);
  }

  @Override
  public ProjectDto partialUpdate(Long id, ProjectDto projectDto) {

    projectDto.setId(id);

    ProjectEntity updatedProjectEntity =
        projectRepository
            .findById(id)
            .map(
                existingProject -> {
                  Optional.ofNullable(projectDto.getProjectName())
                      .ifPresent(existingProject::setProjectName);
                  return projectRepository.save(existingProject);
                })
            .orElseThrow(() -> new ProjectNotFoundException(id));

    return projectMapper.mapTo(updatedProjectEntity);
  }

  @Override
  public void delete(Long id) {
    if (!isExists(id)) {
      throw new ProjectNotFoundException(id);
    }
    projectRepository.deleteById(id);
  }

  @Override
  public ProjectDto updateSkills(Long projectId, List<Long> skillIds) {
    ProjectEntity project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));

    project.setSkillIds(skillIds);

    ProjectEntity saved = projectRepository.save(project);
    return projectMapper.mapTo(saved);
  }
}
