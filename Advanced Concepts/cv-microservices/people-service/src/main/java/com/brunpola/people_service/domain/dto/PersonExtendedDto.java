package com.brunpola.people_service.domain.dto;

import com.brunpola.people_service.domain.external.ProjectDto;
import com.brunpola.people_service.domain.external.SkillDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonExtendedDto {

  private Long id;

  private String firstName;
  private String lastName;

  private List<ProjectDto> projects;
  private List<SkillDto> skills;
}
