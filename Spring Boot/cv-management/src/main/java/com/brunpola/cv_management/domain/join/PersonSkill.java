package com.brunpola.cv_management.domain.join;

import com.brunpola.cv_management.domain.entities.PersonEntity;
import com.brunpola.cv_management.domain.entities.SkillEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/** TEST */
@Entity
@NoArgsConstructor
@Data
@Table(name = "PersonSkills")
public class PersonSkill {
  /** TEST */
  @EmbeddedId private PersonSkillId id;

  /** TEST */
  @ManyToOne
  @MapsId("personId")
  @JoinColumn(name = "PersonID")
  private PersonEntity person;

  /** TEST */
  @ManyToOne
  @MapsId("skillId")
  @JoinColumn(name = "SkillID")
  private SkillEntity skill;

  /**
   * TEST
   *
   * @param person person
   * @param skill skill
   */
  public PersonSkill(PersonEntity person, SkillEntity skill) {
    this.person = person;
    this.skill = skill;
    this.id = new PersonSkillId(person.getId(), skill.getId());
  }
}
