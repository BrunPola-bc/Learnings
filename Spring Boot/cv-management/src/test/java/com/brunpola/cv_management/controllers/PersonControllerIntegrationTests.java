package com.brunpola.cv_management.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.brunpola.cv_management.TestAuthUtil;
import com.brunpola.cv_management.TestDataUtil;
import com.brunpola.cv_management.config.TestAuthUtilConfig;
import com.brunpola.cv_management.domain.dto.PersonDto;
import com.brunpola.cv_management.domain.entities.PersonEntity;
import com.brunpola.cv_management.domain.entities.SkillEntity;
import com.brunpola.cv_management.domain.join.PersonSkill;
import com.brunpola.cv_management.domain.join.PersonSkillId;
import com.brunpola.cv_management.exceptions.base.NotFoundException;
import com.brunpola.cv_management.repositories.PersonSkillRepository;
import com.brunpola.cv_management.security.model.Role;
import com.brunpola.cv_management.services.PersonService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@Import(TestAuthUtilConfig.class)
class PersonControllerIntegrationTests {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;
  private final PersonSkillRepository personSkillRepository;
  private final PersonService personService;
  private final TestAuthUtil testAuthUtil;

  @Autowired
  public PersonControllerIntegrationTests(
      MockMvc mockMvc,
      PersonService personService,
      PersonSkillRepository personSkillRepository,
      TestAuthUtil testAuthUtil) {
    this.mockMvc = mockMvc;
    this.objectMapper = new ObjectMapper();
    this.personService = personService;
    this.personSkillRepository = personSkillRepository;
    this.testAuthUtil = testAuthUtil;
  }

  private String jwtToken;

  @BeforeEach
  void setUp() throws Exception {
    jwtToken = testAuthUtil.registerUser(Role.ADMIN);
  }

  @Test
  void TestThatCreatePersonSuccessfullyReturns201Created() throws Exception {

    PersonEntity person = TestDataUtil.createTestPersonA();
    assertThat(person.getId()).isNull();
    String personJson = objectMapper.writeValueAsString(person);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/people")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .content(personJson))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void TestThatCreatePersonSuccessfullyReturnsSavedPerson() throws Exception {

    PersonEntity person = TestDataUtil.createTestPersonA();
    assertThat(person.getId()).isNull();
    String personJson = objectMapper.writeValueAsString(person);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/people")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .content(personJson))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(person.getFirstName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(person.getLastName()));
  }

  @Test
  void TestThatInvalidPersonDataReturns400BadRequestWithExpectedMessages() throws Exception {

    PersonEntity person = TestDataUtil.createTestPersonA();
    person.setFirstName(null);
    assertThat(person.getId()).isNull();
    assertThat(person.getFirstName()).isNull();
    String invalidJson = objectMapper.writeValueAsString(person);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/people")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message.firstName").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message.lastName").doesNotExist());
  }

  @Test
  void TestThatListPeopleReturnsHttpStatus200() throws Exception {

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void TestThatListPeopleReturnsListOfPeople() throws Exception {
    PersonEntity person1 = personService.findOne(1L);
    PersonEntity person2 = personService.findOne(2L);
    PersonEntity person3 = personService.findOne(3L);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(person1.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].firstName").value(person1.getFirstName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].lastName").value(person1.getLastName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(person2.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].firstName").value(person2.getFirstName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].lastName").value(person2.getLastName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[2].id").value(person3.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[2].firstName").value(person3.getFirstName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[2].lastName").value(person3.getLastName()));
  }

  @Test
  void testListPeoplePagedReturnsHttpStatus200() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people/paged")
                .param("page", "0") // page number
                .param("size", "10") // page size
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void testListPeoplePagedReturnsExpectedContent() throws Exception {
    // Prepare pageable and fetch expected entities
    Pageable pageable = PageRequest.of(0, 10);
    var pagedPeople = personService.findAll(pageable).getContent();

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people/paged")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(pagedPeople.size()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.content[0].id").value(pagedPeople.get(0).getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.content[0].firstName")
                .value(pagedPeople.get(0).getFirstName()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.content[0].lastName")
                .value(pagedPeople.get(0).getLastName()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.content[1].id").value(pagedPeople.get(1).getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.content[1].firstName")
                .value(pagedPeople.get(1).getFirstName()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.content[1].lastName")
                .value(pagedPeople.get(1).getLastName()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.content[2].id").value(pagedPeople.get(2).getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.content[2].firstName")
                .value(pagedPeople.get(2).getFirstName()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.content[2].lastName")
                .value(pagedPeople.get(2).getLastName()))
    // Repeat for as many items as needed or iterate dynamically
    ;
  }

  @Test
  void TestThatGetPersonReturnsHttpStatus200WhenPersonExists() throws Exception {
    PersonEntity person = TestDataUtil.createTestPersonA();
    assertThat(person.getId()).isNull();
    person = personService.save(person);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people/" + person.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isOk())
    // .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
    // .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(person.getFirstName()))
    // .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(person.getLastName()))
    ;
  }

  @Test
  void TestThatGetPersonReturnsHttpStatus404WhenPersonDoesNotExist() throws Exception {

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
    // .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
    // .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(person.getFirstName()))
    // .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(person.getLastName()))
    ;
  }

  @Test
  void TestThatGetPersonReturnsCorrectPerson() throws Exception {
    PersonEntity person = TestDataUtil.createTestPersonA();
    assertThat(person.getId()).isNull();
    person = personService.save(person);
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people/" + person.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        // .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(person.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(person.getFirstName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(person.getLastName()));
  }

  @Test
  void TestFullUpdatePersonReturnsHttpStatus404WhenPersonDoesNotExist() throws Exception {
    PersonDto personDto = TestDataUtil.createTestPersonDtoA();
    personDto.setId(-1L);
    String personDtoJson = objectMapper.writeValueAsString(personDto);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/people/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .content(personDtoJson))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  void TestFullUpdatePersonReturnsHttpStatus200WhenPersonExist() throws Exception {
    PersonEntity personEntity = TestDataUtil.createTestPersonA();
    PersonEntity saved = personService.save(personEntity);

    PersonDto personDto = TestDataUtil.createTestPersonDtoA();
    personDto.setId(-1L);
    String personDtoJson = objectMapper.writeValueAsString(personDto);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/people/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .content(personDtoJson))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void TestFullUpdatePersonUpdatesExistingPerson() throws Exception {
    PersonEntity personEntityA = TestDataUtil.createTestPersonA();
    PersonEntity saved = personService.save(personEntityA);

    PersonEntity personEntityB = TestDataUtil.createTestPersonB();
    String personDtoUpdatedJson = objectMapper.writeValueAsString(personEntityB);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/people/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .content(personDtoUpdatedJson))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(saved.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.firstName").value(personEntityB.getFirstName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(personEntityB.getLastName()));
  }

  @Test
  void TestPartialUpdatePersonReturnsHttpStatus200WhenPersonExist() throws Exception {
    PersonEntity personEntity = TestDataUtil.createTestPersonA();
    PersonEntity saved = personService.save(personEntity);

    PersonDto personDto = TestDataUtil.createTestPersonDtoA();
    personDto.setId(-1L);
    String personDtoJson = objectMapper.writeValueAsString(personDto);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/people/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .content(personDtoJson))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void TestPartialUpdatePersonReturnsHttpStatus404WhenPersonDoesNotExist() throws Exception {
    PersonDto personDto = TestDataUtil.createTestPersonDtoA();
    personDto.setId(-1L);
    String personDtoJson = objectMapper.writeValueAsString(personDto);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/people/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .content(personDtoJson))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  void TestPartialUpdatePersonUpdatesExistingPerson() throws Exception {
    PersonEntity personEntityA = TestDataUtil.createTestPersonA();
    PersonEntity saved = personService.save(personEntityA);

    PersonEntity personEntityB = TestDataUtil.createTestPersonB();
    personEntityB.setFirstName(null);
    String personDtoUpdatedJson = objectMapper.writeValueAsString(personEntityB);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/people/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .content(personDtoUpdatedJson))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(saved.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(saved.getFirstName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(personEntityB.getLastName()));
  }

  @Test
  void TestDeletePersonReturnsHttpStatus204NoContentWhenPersonExists() throws Exception {
    PersonEntity personEntityA = TestDataUtil.createTestPersonA();
    PersonEntity saved = personService.save(personEntityA);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/people/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  void TestDeletePersonReturnsHttpStatus404NotFoundWhenPersonDoesNotExist() throws Exception {

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/people/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  void TestDeletePersonDeletesPerson() throws Exception {
    PersonEntity personEntityA = TestDataUtil.createTestPersonA();
    PersonEntity saved = personService.save(personEntityA);
    Long savedId = saved.getId();

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/people/" + savedId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isNoContent());

    assertThatThrownBy(() -> personService.findOne(savedId)).isInstanceOf(NotFoundException.class);
  }

  @Test
  @Transactional
  void TestThatDeletePersonServiceCascadeDeletes() throws Exception {
    PersonEntity person = personService.findOne(1L);

    PersonSkill firsPersonSkill = person.getSkills().stream().findFirst().orElseThrow();
    SkillEntity skill = firsPersonSkill.getSkill();

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/people/" + person.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isNoContent());

    boolean personSkillExists =
        personSkillRepository.existsById(new PersonSkillId(person.getId(), skill.getId()));

    assertThat(personSkillExists).isFalse();
  }

  @Test
  void TestThatListPeopleExtendedReturnsHttpStatus200() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people/extended")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void TestThatListPeopleExtendedReturnsPeopleWithSkillsAndProjects() throws Exception {

    PersonEntity person = personService.findOne(1L);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people/extended")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(person.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value(person.getFirstName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName").value(person.getLastName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].skills").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].projects").isArray());
  }

  @Test
  void TestThatGetPersonExtendedReturnsHttpStatus200WhenPersonExists() throws Exception {
    PersonEntity person = personService.save(TestDataUtil.createTestPersonA());

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people/" + person.getId() + "/extended")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void TestThatGetPersonExtendedReturnsCorrectPerson() throws Exception {
    PersonEntity person = personService.save(TestDataUtil.createTestPersonA());

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people/" + person.getId() + "/extended")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(person.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(person.getFirstName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(person.getLastName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.skills").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("$.projects").isArray());
  }

  @Test
  void TestThatGetPersonExtendedReturnsHttpStatus404WhenPersonDoesNotExist() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people/-1/extended")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @Transactional
  void TestThatGetPersonExtendedReturnsMappedSkillsAndProjects() throws Exception {
    PersonEntity person = personService.findOne(1L);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people/" + person.getId() + "/extended")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isOk())
        // Skills exist
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.skills.length()").value(person.getSkills().size()))
        // Projects exist
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.projects.length()")
                .value(person.getProjects().size()));
  }

  @Test
  void TestThatSearchPeopleByExampleFiltersCorrectly() throws Exception {

    // Arrange: create 3 people
    PersonEntity personA = TestDataUtil.createTestPersonA();
    PersonEntity savedA = personService.save(personA);

    PersonEntity personB = TestDataUtil.createTestPersonB();
    personB.setFirstName("NOTFOUND");
    // PersonEntity savedB =
    personService.save(personB);

    PersonEntity personC = TestDataUtil.createTestPersonA();
    PersonEntity savedC = personService.save(personC);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people/search")
                .param("firstName", "uno") // works with this if i remove @RequestBody in controller
                // .param("lastName", "pol")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
            // .content(searchExampleJson) // works with this if i add @RequestBody in
            // controller
            )
        .andExpect(MockMvcResultMatchers.status().isOk())
        // expect only matching people
        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(savedA.getId()))
        // .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(savedB.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(savedC.getId()));
  }
}
