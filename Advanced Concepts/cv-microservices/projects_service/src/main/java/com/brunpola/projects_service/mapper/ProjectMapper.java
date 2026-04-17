package com.brunpola.projects_service.mapper;

import com.brunpola.projects_service.domain.dto.ProjectDto;
import com.brunpola.projects_service.domain.dto.ProjectExtendedDto;
import com.brunpola.projects_service.domain.entity.ProjectEntity;
import com.brunpola.projects_service.domain.external.PersonDto;
import com.brunpola.projects_service.domain.external.SkillDto;
import java.util.List;

public interface ProjectMapper {

  ProjectDto toDto(ProjectEntity entity);

  ProjectExtendedDto toExtendedDto(
      ProjectEntity entity, List<PersonDto> personDtos, List<SkillDto> skillDtos);

  ProjectEntity toEntity(ProjectDto dto);
}
