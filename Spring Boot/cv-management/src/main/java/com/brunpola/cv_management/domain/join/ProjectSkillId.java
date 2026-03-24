package com.brunpola.cv_management.domain.join;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** TEST */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSkillId implements Serializable {
  /** TEST */
  private Long projectId;

  /** TEST */
  private Long skillId;
}
