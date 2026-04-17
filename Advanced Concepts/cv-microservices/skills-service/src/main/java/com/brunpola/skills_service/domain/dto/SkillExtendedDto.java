package com.brunpola.skills_service.domain.dto;

import com.brunpola.skills_service.domain.external.PersonDto;
import com.brunpola.skills_service.domain.external.ProjectDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkillExtendedDto {

  private Long id;

  private String skillName;

  private List<PersonDto> people;
  private List<ProjectDto> projects;
}
