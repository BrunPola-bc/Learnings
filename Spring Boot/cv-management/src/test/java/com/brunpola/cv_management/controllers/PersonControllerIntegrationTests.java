package com.brunpola.cv_management.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunpola.cv_management.TestDataUtil;
import com.brunpola.cv_management.domain.dto.PersonDto;
import com.brunpola.cv_management.domain.entities.PersonEntity;
import com.brunpola.cv_management.repositories.PersonRepository;
import com.brunpola.cv_management.services.PersonService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
// @ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class PersonControllerIntegrationTests {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;
  private final PersonRepository personRepository;
  private final PersonService personService;

  @Autowired
  public PersonControllerIntegrationTests(
      MockMvc mockMvc, PersonRepository personRepository, PersonService personService) {
    this.mockMvc = mockMvc;
    this.objectMapper = new ObjectMapper();
    this.personRepository = personRepository;
    this.personService = personService;
  }

  @Test
  public void TestThatCreatePersonSuccessfullyReturns201Created() throws Exception {

    PersonEntity person = TestDataUtil.createTestPersonA();
    assertThat(person.getId() == null);
    String personJson = objectMapper.writeValueAsString(person);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/people")
                .contentType(MediaType.APPLICATION_JSON)
                .content(personJson))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  public void TestThatCreatePersonSuccessfullyReturnsSavedPerson() throws Exception {

    PersonEntity person = TestDataUtil.createTestPersonA();
    assertThat(person.getId() == null);
    String personJson = objectMapper.writeValueAsString(person);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/people")
                .contentType(MediaType.APPLICATION_JSON)
                .content(personJson))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(person.getFirstName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(person.getLastName()));
  }

  @Test
  public void TestThatListPeopleReturnsHttpStatus200() throws Exception {

    mockMvc
        .perform(MockMvcRequestBuilders.get("/people").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void TestThatListPeopleReturnsListOfPeople() throws Exception {
    PersonEntity person1 = personRepository.findById(1L).orElseThrow();
    PersonEntity person2 = personRepository.findById(2L).orElseThrow();
    PersonEntity person3 = personRepository.findById(3L).orElseThrow();

    mockMvc
        .perform(MockMvcRequestBuilders.get("/people").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(person1.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value(person1.getFirstName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName").value(person1.getLastName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(person2.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName").value(person2.getFirstName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].lastName").value(person2.getLastName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value(person3.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[2].firstName").value(person3.getFirstName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[2].lastName").value(person3.getLastName()));
  }

  @Test
  public void TestThatGetPersonReturnsHttpStatus200WhenPersonExists() throws Exception {
    PersonEntity person = TestDataUtil.createTestPersonA();
    assertThat(person.getId() == null);
    person = personService.save(person);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people/" + person.getId())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
    // .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
    // .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(person.getFirstName()))
    // .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(person.getLastName()))
    ;
  }

  @Test
  public void TestThatGetPersonReturnsHttpStatus404WhenPersonDoesNotExist() throws Exception {

    mockMvc
        .perform(MockMvcRequestBuilders.get("/people/-1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
    // .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
    // .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(person.getFirstName()))
    // .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(person.getLastName()))
    ;
  }

  @Test
  public void TestThatGetPersonReturnsCorrectPerson() throws Exception {
    PersonEntity person = TestDataUtil.createTestPersonA();
    assertThat(person.getId() == null);
    person = personService.save(person);
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people/" + person.getId())
                .contentType(MediaType.APPLICATION_JSON))
        // .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(person.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(person.getFirstName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(person.getLastName()));
  }

  @Test
  public void TestFullUpdatePersonReturnsHttpStatus404WhenPersonDoesNotExist() throws Exception {
    PersonDto personDto = TestDataUtil.createTestPersonDtoA();
    personDto.setId(-1L);
    String personDtoJson = objectMapper.writeValueAsString(personDto);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/people/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(personDtoJson))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void TestFullUpdatePersonReturnsHttpStatus200WhenPersonExist() throws Exception {
    PersonEntity personEntity = TestDataUtil.createTestPersonA();
    PersonEntity saved = personService.save(personEntity);

    PersonDto personDto = TestDataUtil.createTestPersonDtoA();
    personDto.setId(-1L);
    String personDtoJson = objectMapper.writeValueAsString(personDto);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/people/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(personDtoJson))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void TestFullUpdatePersonUpdatesExistingPerson() throws Exception {
    PersonEntity personEntityA = TestDataUtil.createTestPersonA();
    PersonEntity saved = personService.save(personEntityA);

    PersonEntity personEntityB = TestDataUtil.createTestPersonB();
    // personEntityB.setId(saved.getId());
    String personDtoUpdatedJson = objectMapper.writeValueAsString(personEntityB);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/people/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(personDtoUpdatedJson))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(saved.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.firstName").value(personEntityB.getFirstName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(personEntityB.getLastName()));
  }

  @Test
  public void TestPartialUpdatePersonReturnsHttpStatus200WhenPersonExist() throws Exception {
    PersonEntity personEntity = TestDataUtil.createTestPersonA();
    PersonEntity saved = personService.save(personEntity);

    PersonDto personDto = TestDataUtil.createTestPersonDtoA();
    personDto.setId(-1L);
    String personDtoJson = objectMapper.writeValueAsString(personDto);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/people/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(personDtoJson))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void TestPartialUpdatePersonReturnsHttpStatus404WhenPersonDoesNotExist() throws Exception {
    PersonDto personDto = TestDataUtil.createTestPersonDtoA();
    personDto.setId(-1L);
    String personDtoJson = objectMapper.writeValueAsString(personDto);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/people/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(personDtoJson))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void TestPartialUpdatePersonUpdatesExistingPerson() throws Exception {
    PersonEntity personEntityA = TestDataUtil.createTestPersonA();
    PersonEntity saved = personService.save(personEntityA);

    PersonEntity personEntityB = TestDataUtil.createTestPersonB();
    // personEntityB.setId(saved.getId());
    personEntityB.setFirstName(null);
    String personDtoUpdatedJson = objectMapper.writeValueAsString(personEntityB);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/people/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(personDtoUpdatedJson))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(saved.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(saved.getFirstName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(personEntityB.getLastName()));
  }

  @Test
  public void TestDeletePersonReturnsHttpStatus204NoContent() throws Exception {
    PersonEntity personEntityA = TestDataUtil.createTestPersonA();
    PersonEntity saved = personService.save(personEntityA);

    personService.delete(saved.getId());

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/people/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  public void TestDeletePersonDeletesPerson() throws Exception {
    PersonEntity personEntityA = TestDataUtil.createTestPersonA();
    PersonEntity saved = personService.save(personEntityA);

    personService.delete(saved.getId());

    Optional<PersonEntity> deletedPerson = personService.findOne(saved.getId());

    assertThat(deletedPerson).isEmpty();

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/people/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }
}
