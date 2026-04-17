package com.brunpola.people_service.mapper;

import com.brunpola.people_service.domain.dto.PersonDto;
import com.brunpola.people_service.domain.dto.PersonExtendedDto;
import com.brunpola.people_service.domain.entity.PersonEntity;
import com.brunpola.people_service.domain.external.ProjectDto;
import com.brunpola.people_service.domain.external.SkillDto;
import java.util.List;

public interface PersonMapper {

  PersonDto toDto(PersonEntity entity);

  PersonExtendedDto toExtendedDto(
      PersonEntity entity, List<ProjectDto> projectDtos, List<SkillDto> skillDtos);

  PersonEntity toEntity(PersonDto dto);
}
