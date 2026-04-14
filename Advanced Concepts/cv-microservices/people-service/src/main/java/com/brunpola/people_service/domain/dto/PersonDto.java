package com.brunpola.people_service.domain.dto;

import jakarta.validation.constraints.NotNull;
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

  @NotNull private String firstName;
  @NotNull private String lastName;
}
