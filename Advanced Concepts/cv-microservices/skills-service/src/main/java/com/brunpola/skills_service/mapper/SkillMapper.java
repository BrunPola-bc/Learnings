package com.brunpola.skills_service.mapper;

import com.brunpola.skills_service.domain.dto.SkillDto;
import com.brunpola.skills_service.domain.dto.SkillExtendedDto;
import com.brunpola.skills_service.domain.entity.SkillEntity;
import com.brunpola.skills_service.domain.external.PersonDto;
import com.brunpola.skills_service.domain.external.ProjectDto;
import java.util.List;

public interface SkillMapper {

  SkillDto toDto(SkillEntity entity);

  SkillExtendedDto toExtendedDto(
      SkillEntity entity, List<PersonDto> personDtos, List<ProjectDto> projectDtos);

  SkillEntity toEntity(SkillDto dto);
}
