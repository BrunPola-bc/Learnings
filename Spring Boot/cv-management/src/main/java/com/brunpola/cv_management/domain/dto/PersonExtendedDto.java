package com.brunpola.cv_management.domain.dto;

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

  private List<SkillDto> skills;

  private List<ProjectDto> projects;
}
