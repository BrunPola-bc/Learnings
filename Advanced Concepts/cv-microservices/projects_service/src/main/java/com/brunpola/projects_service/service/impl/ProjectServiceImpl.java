package com.brunpola.projects_service.service.impl;

import com.brunpola.projects_service.client.PeopleHttpClient;
import com.brunpola.projects_service.client.SkillHttpClient;
import com.brunpola.projects_service.domain.dto.ProjectDto;
import com.brunpola.projects_service.domain.dto.ProjectExtendedDto;
import com.brunpola.projects_service.domain.entity.ProjectEntity;
import com.brunpola.projects_service.domain.external.PersonDto;
import com.brunpola.projects_service.domain.external.SkillDto;
import com.brunpola.projects_service.exception.ProjectNotFoundException;
import com.brunpola.projects_service.mapper.ProjectMapper;
import com.brunpola.projects_service.repository.ProjectRepository;
import com.brunpola.projects_service.service.ProjectService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {

  private final ProjectRepository projectRepository;
  private final ProjectMapper projectMapper;
  private final PeopleHttpClient peopleClient;
  private final SkillHttpClient skillClient;

  public ProjectServiceImpl(
      ProjectRepository projectRepository,
      ProjectMapper projectMapper,
      PeopleHttpClient peopleClient,
      SkillHttpClient skillClient) {
    this.projectRepository = projectRepository;
    this.projectMapper = projectMapper;
    this.peopleClient = peopleClient;
    this.skillClient = skillClient;
  }

  @Override
  public ProjectDto save(ProjectDto projectDto) {
    ProjectEntity projectEntity = projectMapper.toEntity(projectDto);
    projectEntity = projectRepository.save(projectEntity);
    return projectMapper.toDto(projectEntity);
  }

  @Override
  public ProjectDto update(ProjectDto projectDto) {
    if (!isExists(projectDto.getId())) {
      throw new ProjectNotFoundException(projectDto.getId());
    }

    ProjectEntity projectEntity = projectMapper.toEntity(projectDto);
    projectEntity = projectRepository.save(projectEntity);
    return projectMapper.toDto(projectEntity);
  }

  @Override
  public List<ProjectDto> findAll() {
    return projectRepository.findAll().stream().map(projectMapper::toDto).toList();
  }

  @Override
  public ProjectDto findOne(Long id) {
    ProjectEntity projectEntity =
        projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));
    return projectMapper.toDto(projectEntity);
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

    return projectMapper.toDto(updatedProjectEntity);
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
    return projectMapper.toDto(saved);
  }

  @Override
  public List<ProjectDto> findBySkillId(Long skillId) {
    List<ProjectEntity> bySkillIdsContaining = projectRepository.findBySkillIdsContaining(skillId);
    return bySkillIdsContaining.stream().map(projectMapper::toDto).toList();
  }

  @Override
  public ProjectExtendedDto findOneExtended(Long id, String authHeader) {
    ProjectEntity projectEntity =
        projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));

    List<PersonDto> people = peopleClient.findPeopleByProjectId(id, authHeader);
    List<Long> skillIds = projectEntity.getSkillIds();
    List<SkillDto> skills =
        skillIds.isEmpty() ? List.of() : skillClient.getSkillsByIds(skillIds, authHeader);

    return projectMapper.toExtendedDto(projectEntity, people, skills);
  }

  @Override
  public List<ProjectExtendedDto> findAllExtended(String authHeader) {

    List<ProjectEntity> projectEntities = projectRepository.findAll();

    Set<Long> allSkillIds =
        projectEntities.stream()
            .flatMap(entity -> entity.getSkillIds().stream())
            .collect(Collectors.toSet());

    Map<Long, SkillDto> skillDtoMap =
        skillClient.getSkillsByIds(new ArrayList<>(allSkillIds), authHeader).stream()
            .collect(Collectors.toMap(SkillDto::getId, skill -> skill));

    return projectEntities.stream()
        .map(
            projectEntity -> {
              List<PersonDto> people =
                  peopleClient.findPeopleByProjectId(projectEntity.getId(), authHeader);
              List<SkillDto> skills =
                  projectEntity.getSkillIds().stream()
                      .map(skillDtoMap::get)
                      .filter(Objects::nonNull)
                      .toList();

              return projectMapper.toExtendedDto(projectEntity, people, skills);
            })
        .toList();
  }

  @Override
  public List<ProjectDto> findByIds(List<Long> ids) {
    List<ProjectEntity> projects = projectRepository.findAllById(ids);
    return projects.stream().map(projectMapper::toDto).toList();
  }
}
