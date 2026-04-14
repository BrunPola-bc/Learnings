package com.brunpola.people_service.domain.dto;

import jakarta.validation.constraints.NotNull;
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

  @NotNull private String firstName;
  @NotNull private String lastName;

  private List<Long> projectIds;
  private List<Long> skillIds;
}
