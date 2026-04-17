package com.brunpola.projects_service.domain.dto;

import com.brunpola.projects_service.domain.external.PersonDto;
import com.brunpola.projects_service.domain.external.SkillDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectExtendedDto {
  private Long id;

  private String projectName;

  private List<PersonDto> people;
  private List<SkillDto> skills;
}
