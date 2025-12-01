package com.brunpola.cv_management.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonDto {

  private Long id;

  private String firstName;

  private String lastName;

  // @Builder.Default
  // private Set<PersonSkill> skills = new HashSet<>();

  // @Builder.Default
  // private Set<PersonProject> projects = new HashSet<>();
}
