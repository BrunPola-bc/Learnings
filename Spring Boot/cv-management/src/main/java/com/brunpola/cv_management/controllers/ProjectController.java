package com.brunpola.cv_management.controllers;

import com.brunpola.cv_management.domain.dto.ProjectDto;
import com.brunpola.cv_management.domain.entities.ProjectEntity;
import com.brunpola.cv_management.mappers.Mapper;
import com.brunpola.cv_management.services.ProjectService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** TEST */
@RestController
public class ProjectController {

  private final ProjectService projectService;
  private final Mapper<ProjectEntity, ProjectDto> projectMapper;

  /**
   * TEST
   *
   * @param projectService projectService
   * @param projectMapper projectMapper
   */
  public ProjectController(
      ProjectService projectService, Mapper<ProjectEntity, ProjectDto> projectMapper) {
    this.projectService = projectService;
    this.projectMapper = projectMapper;
  }

  /**
   * TEST
   *
   * @param projectDto projectDto
   * @return created project as DTO with generated ID
   */
  @PostMapping(path = "/projects")
  public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto) {
    ProjectEntity projectEntity = projectMapper.mapFrom(projectDto);
    ProjectEntity savedProjectEntity = projectService.createProject(projectEntity);
    return new ResponseEntity<>(projectMapper.mapTo(savedProjectEntity), HttpStatus.CREATED);
  }

  /**
   * TEST
   *
   * @return list of all projects as DTOs
   */
  @GetMapping("/projects")
  public List<ProjectDto> listProjects() {
    List<ProjectEntity> projects = projectService.findAll();
    return projects.stream().map(projectMapper::mapTo).toList();
  }
}
