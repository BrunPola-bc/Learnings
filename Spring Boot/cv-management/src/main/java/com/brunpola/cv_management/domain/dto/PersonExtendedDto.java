package com.brunpola.cv_management.domain.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** TEST */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonExtendedDto {
  /** TEST */
  private Long id;

  /** TEST */
  private String firstName;

  /** TEST */
  private String lastName;

  /** TEST */
  private List<SkillDto> skills;

  /** TEST */
  private List<ProjectDto> projects;
}
