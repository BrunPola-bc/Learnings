package com.brunpola.projects_service.service;

import com.brunpola.projects_service.domain.dto.ProjectDto;
import com.brunpola.projects_service.domain.dto.ProjectExtendedDto;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;

public interface ProjectService {

  @Validated
  ProjectDto save(@Valid ProjectDto project);

  ProjectDto update(ProjectDto project);

  List<ProjectDto> findAll();

  ProjectDto findOne(Long id);

  boolean isExists(Long id);

  ProjectDto partialUpdate(Long id, ProjectDto projectDto);

  void delete(Long id);

  ProjectDto updateSkills(Long id, List<Long> ids);

  List<ProjectDto> findBySkillId(Long skillId);

  ProjectExtendedDto findOneExtended(Long id);

  List<ProjectExtendedDto> findAllExtended();

  List<ProjectDto> findByIds(List<Long> ids);
}
