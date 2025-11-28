package com.brunpola.cv_management.domain.join;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonSkillId implements Serializable {

  private Long personId;
  private Long skillId;
}
