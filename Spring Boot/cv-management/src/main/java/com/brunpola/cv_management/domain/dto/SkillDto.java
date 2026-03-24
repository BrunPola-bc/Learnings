package com.brunpola.cv_management.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** TEST */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkillDto {
  /** TEST */
  private Long id;

  /** TEST */
  private String skillName;
}
