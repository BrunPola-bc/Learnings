package com.brunpola.cv_management.domain.join;

import com.brunpola.cv_management.domain.Person;
import com.brunpola.cv_management.domain.Skill;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "PersonSkills")
public class PersonSkill {

  @EmbeddedId private PersonSkillId id;

  @ManyToOne
  @MapsId("personId")
  @JoinColumn(name = "PersonID")
  private Person person;

  @ManyToOne
  @MapsId("skillId")
  @JoinColumn(name = "SkillID")
  private Skill skill;

  public PersonSkill(Person person, Skill skill) {
    this.person = person;
    this.skill = skill;
    this.id = new PersonSkillId(person.getId(), skill.getId());
  }
}
