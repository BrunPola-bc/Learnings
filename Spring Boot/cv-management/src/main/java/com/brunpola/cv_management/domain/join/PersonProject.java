package com.brunpola.cv_management.domain.join;

import com.brunpola.cv_management.domain.Person;
import com.brunpola.cv_management.domain.Project;
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
@Table(name = "PersonProjects")
public class PersonProject {

  @EmbeddedId private PersonProjectId id;

  @ManyToOne
  @MapsId("personId")
  @JoinColumn(name = "PersonID")
  private Person person;

  @ManyToOne
  @MapsId("projectId")
  @JoinColumn(name = "ProjectID")
  private Project project;

  public PersonProject(Person person, Project project) {
    this.person = person;
    this.project = project;
    this.id = new PersonProjectId(person.getId(), project.getId());
  }
}
