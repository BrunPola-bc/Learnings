package com.brunpola.projects_service.controller;

import com.brunpola.projects_service.domain.dto.IdsRequestDto;
import com.brunpola.projects_service.domain.dto.ProjectDto;
import com.brunpola.projects_service.domain.dto.ProjectExtendedDto;
import com.brunpola.projects_service.service.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

  private final ProjectService projectService;

  public ProjectController(ProjectService projectService) {
    this.projectService = projectService;
  }

  @PostMapping
  public ResponseEntity<ProjectDto> createProject(@RequestBody @Valid ProjectDto projectDto) {
    ProjectDto savedProjectDto = projectService.save(projectDto);
    return new ResponseEntity<>(savedProjectDto, HttpStatus.CREATED);
  }

  @GetMapping
  public List<ProjectDto> listProjects() {
    return projectService.findAll();
  }

  @GetMapping(path = "/{id}")
  public ProjectDto getProject(@PathVariable("id") Long id) {
    return projectService.findOne(id);
  }

  @PutMapping("/{id}")
  public ProjectDto fullUpdateProject(
      @PathVariable("id") Long id, @RequestBody ProjectDto projectDto) {

    projectDto.setId(id);
    return projectService.update(projectDto);
  }

  @PatchMapping(path = "/{id}")
  public ProjectDto partialUpdateProject(
      @PathVariable("id") Long id, @RequestBody ProjectDto projectDto) {

    return projectService.partialUpdate(id, projectDto);
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> deleteProject(@PathVariable("id") Long id) {
    projectService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PutMapping("/{id}/skills")
  public ProjectDto updateSkills(@PathVariable Long id, @RequestBody IdsRequestDto request) {
    return projectService.updateSkills(id, request.getIds());
  }

  @GetMapping("/by-skill/{skillId}")
  public List<ProjectDto> getProjectsBySkillId(@PathVariable Long skillId) {
    return projectService.findBySkillId(skillId);
  }

  @GetMapping("/extended")
  public List<ProjectExtendedDto> listProjectsExtended(
      @RequestHeader("Authorization") String authHeader) {
    return projectService.findAllExtended(authHeader);
  }

  @GetMapping("/{id}/extended")
  public ProjectExtendedDto getProjectExtended(
      @PathVariable("id") Long id, @RequestHeader("Authorization") String authHeader) {
    return projectService.findOneExtended(id, authHeader);
  }

  @GetMapping("/by-ids")
  public List<ProjectDto> getProjectsByIds(@RequestParam List<Long> ids) {
    return projectService.findByIds(ids);
  }
}
