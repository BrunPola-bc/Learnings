package com.brunpola.cv_management.controllers;

import com.brunpola.cv_management.domain.dto.ProjectDto;
import com.brunpola.cv_management.domain.entities.ProjectEntity;
import com.brunpola.cv_management.mappers.Mapper;
import com.brunpola.cv_management.services.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectController {

  private final ProjectService projectService;
  private final Mapper<ProjectEntity, ProjectDto> projectMapper;

  public ProjectController(
      ProjectService projectService, Mapper<ProjectEntity, ProjectDto> projectMapper) {
    this.projectService = projectService;
    this.projectMapper = projectMapper;
  }

  @PostMapping(path = "/projects")
  public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto) {
    ProjectEntity projectEntity = projectMapper.mapFrom(projectDto);
    ProjectEntity savedProjectEntity = projectService.createProject(projectEntity);
    return new ResponseEntity<>(projectMapper.mapTo(savedProjectEntity), HttpStatus.CREATED);
  }
}
