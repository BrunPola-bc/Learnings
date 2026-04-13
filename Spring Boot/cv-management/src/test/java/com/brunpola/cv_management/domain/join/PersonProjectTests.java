package com.brunpola.cv_management.domain.join;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunpola.cv_management.domain.entities.PersonEntity;
import com.brunpola.cv_management.domain.entities.ProjectEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PersonProjectTests {

  @Test
  void testConstructorSetsFieldsCorrectly() {
    PersonEntity person = new PersonEntity();
    person.setId(1L);

    ProjectEntity project = new ProjectEntity();
    project.setId(2L);

    PersonProject personProject = new PersonProject(person, project);

    assertThat(personProject.getPerson()).isEqualTo(person);
    assertThat(personProject.getProject()).isEqualTo(project);
    assertThat(personProject.getId()).isNotNull();
    assertThat(personProject.getId().getPersonId()).isEqualTo(1L);
    assertThat(personProject.getId().getProjectId()).isEqualTo(2L);
  }
}
