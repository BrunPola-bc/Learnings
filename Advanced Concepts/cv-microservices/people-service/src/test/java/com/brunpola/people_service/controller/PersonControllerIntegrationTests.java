package com.brunpola.people_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.brunpola.people_service.TestDataUtil;
import com.brunpola.people_service.client.ProjectHttpClient;
import com.brunpola.people_service.client.SkillHttpClient;
import com.brunpola.people_service.domain.dto.IdsRequestDto;
import com.brunpola.people_service.domain.dto.PersonDto;
import com.brunpola.people_service.domain.dto.PersonExtendedDto;
import com.brunpola.people_service.domain.entity.PersonEntity;
import com.brunpola.people_service.domain.external.ProjectDto;
import com.brunpola.people_service.domain.external.SkillDto;
import com.brunpola.people_service.repository.PersonRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Log
class PersonControllerIntegrationTests {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private PersonRepository personRepository;

  @MockitoBean private ProjectHttpClient projectClient;
  @MockitoBean private SkillHttpClient skillClient;

  private static final int INITIAL_PEOPLE_COUNT = 4;
  private final List<PersonEntity> initialPeople =
      TestDataUtil.samplePeopleEntities(false, INITIAL_PEOPLE_COUNT);

  @BeforeEach
  void setUp() {
    assertNull(initialPeople.getFirst().getId());

    personRepository.saveAll(initialPeople);

    assertNotNull(initialPeople.getFirst().getId());
  }

  @Test
  void contextLoads() {}

  @Test
  void createPerson_returnsCreatedPersonAndStatus201Created() throws Exception {

    PersonDto newPersonDto = TestDataUtil.samplePersonDto(false);

    String responseJson =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/people")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newPersonDto)))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    PersonDto createdPersonDto = objectMapper.readValue(responseJson, PersonDto.class);

    assertNotNull(createdPersonDto.getId());
    assertEquals(newPersonDto.getFirstName(), createdPersonDto.getFirstName());
    assertEquals(newPersonDto.getLastName(), createdPersonDto.getLastName());

    long count = personRepository.count();
    assertEquals(INITIAL_PEOPLE_COUNT + 1, count);
  }

  @Test
  void listPeople_returnsListOfPeopleAndStatus200Ok() throws Exception {

    String responseJson =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/people").contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    List<PersonDto> peopleDtos =
        objectMapper.readValue(
            responseJson,
            objectMapper.getTypeFactory().constructCollectionType(List.class, PersonDto.class));

    int listSize = peopleDtos.size();
    assertEquals(INITIAL_PEOPLE_COUNT, listSize);

    for (int i = 0; i < listSize; i++) {
      PersonDto personDto = peopleDtos.get(i);
      assertNotNull(personDto.getId());
      assertEquals(initialPeople.get(i).getId(), personDto.getId());
      assertEquals(initialPeople.get(i).getFirstName(), personDto.getFirstName());
    }
  }

  @Test
  void listPeople_returnsEmptyListAndStatus200OkWhenNoPeopleInDatabase() throws Exception {

    personRepository.deleteAll();

    String responseJson =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/people").contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    List<PersonDto> peopleDtos =
        objectMapper.readValue(
            responseJson,
            objectMapper.getTypeFactory().constructCollectionType(List.class, PersonDto.class));

    int listSize = peopleDtos.size();
    assertEquals(0, listSize);
    assertEquals(List.of(), peopleDtos);
  }

  @Test
  void getPerson_returnsPersonByIdAndStatus200OkWhenPersonExists() throws Exception {

    final long EXPECTED_ID = 1L;

    String responseJson =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/people/" + EXPECTED_ID)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    PersonDto fetchedPersonDto = objectMapper.readValue(responseJson, PersonDto.class);

    assertEquals(EXPECTED_ID, fetchedPersonDto.getId());
    assertEquals(
        initialPeople.get((int) EXPECTED_ID - 1).getFirstName(), fetchedPersonDto.getFirstName());
  }

  @Test
  void getPerson_returnsStatus404NotFoundWhenPersonDoesNotExist() throws Exception {

    final long EXPECTED_ID = 5L;

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/people/" + EXPECTED_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  void fullUpdatePerson_returnsUpdatedPersonAndStatus200OkWhenPersonExists() throws Exception {

    final long EXPECTED_ID = 1L;

    PersonDto updateDto = TestDataUtil.samplePersonDto(false);
    updateDto.setFirstName("UpdatedFirstName");
    updateDto.setLastName("UpdatedLastName");

    String responseJson =
        mockMvc
            .perform(
                MockMvcRequestBuilders.put("/api/people/" + EXPECTED_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    PersonDto updatedPerson = objectMapper.readValue(responseJson, PersonDto.class);

    assertEquals(EXPECTED_ID, updatedPerson.getId());
    assertEquals("UpdatedFirstName", updatedPerson.getFirstName());
    assertEquals("UpdatedLastName", updatedPerson.getLastName());

    PersonEntity fromDb =
        personRepository
            .findById(EXPECTED_ID)
            .orElseThrow(() -> new AssertionError("Person Not Found"));

    assertEquals("UpdatedFirstName", fromDb.getFirstName());
    assertEquals("UpdatedLastName", fromDb.getLastName());
  }

  @Test
  void fullUpdatePerson_returns404NotFoundWhenPersonDoesNotExist() throws Exception {

    final long EXPECTED_ID = 999L;

    PersonDto updateDto = TestDataUtil.samplePersonDto(false);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/people/" + EXPECTED_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  void partialUpdatePerson_updatesBothFieldsAndReturns200Ok() throws Exception {

    final long EXPECTED_ID = 1L;

    PersonDto patchDto = new PersonDto();
    patchDto.setFirstName("PartiallyUpdatedFirst");
    patchDto.setLastName("PartiallyUpdatedLast");

    String responseJson =
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch("/api/people/" + EXPECTED_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patchDto)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    PersonDto result = objectMapper.readValue(responseJson, PersonDto.class);

    assertEquals("PartiallyUpdatedFirst", result.getFirstName());
    assertEquals("PartiallyUpdatedLast", result.getLastName());
  }

  @Test
  void partialUpdatePerson_updatesOnlyFirstName() throws Exception {

    final long EXPECTED_ID = 1L;

    String originalLastName = initialPeople.get((int) EXPECTED_ID - 1).getLastName();

    PersonDto patchDto = new PersonDto();
    patchDto.setFirstName("OnlyFirstUpdated");

    String responseJson =
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch("/api/people/" + EXPECTED_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patchDto)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    PersonDto result = objectMapper.readValue(responseJson, PersonDto.class);

    assertEquals("OnlyFirstUpdated", result.getFirstName());
    assertEquals(originalLastName, result.getLastName());
  }

  @Test
  void partialUpdatePerson_updatesOnlyLastName() throws Exception {

    final long EXPECTED_ID = 1L;

    String originalFirstName = initialPeople.get((int) EXPECTED_ID - 1).getFirstName();

    PersonDto patchDto = new PersonDto();
    patchDto.setLastName("OnlyLastUpdated");

    String responseJson =
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch("/api/people/" + EXPECTED_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patchDto)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    PersonDto result = objectMapper.readValue(responseJson, PersonDto.class);

    assertEquals(originalFirstName, result.getFirstName());
    assertEquals("OnlyLastUpdated", result.getLastName());
  }

  @Test
  void partialUpdatePerson_updatesNeitherField() throws Exception {

    final long EXPECTED_ID = 1L;

    PersonEntity before =
        personRepository
            .findById(EXPECTED_ID)
            .orElseThrow(() -> new AssertionError("Person Not Found"));

    PersonDto patchDto = new PersonDto();

    String responseJson =
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch("/api/people/" + EXPECTED_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patchDto)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    PersonDto result = objectMapper.readValue(responseJson, PersonDto.class);

    assertEquals(before.getFirstName(), result.getFirstName());
    assertEquals(before.getLastName(), result.getLastName());
  }

  @Test
  void partialUpdatePerson_returns404NotFoundWhenPersonDoesNotExist() throws Exception {

    PersonDto patchDto = new PersonDto();
    patchDto.setFirstName("NoOne");

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/people/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchDto)))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  void deletePerson_deletesPersonAndReturns204NoContent() throws Exception {

    final long EXPECTED_ID = 1L;

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/people/" + EXPECTED_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNoContent());

    assertEquals(false, personRepository.existsById(EXPECTED_ID));
  }

  @Test
  void deletePerson_returns404NotFoundWhenPersonDoesNotExist() throws Exception {

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/people/999")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  void updateSkills_returnsUpdatedPersonAndStatus200OkWhenPersonExists() throws Exception {

    final long EXPECTED_ID = 1L;

    IdsRequestDto idsRequest = IdsRequestDto.builder().ids(List.of(1L, 2L)).build();

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/people/" + EXPECTED_ID + "/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idsRequest)))
        .andExpect(MockMvcResultMatchers.status().isOk());

    PersonEntity updatedPerson =
        personRepository
            .findById(EXPECTED_ID)
            .orElseThrow(() -> new AssertionError("Person Not Found"));

    assertEquals(idsRequest.getIds(), updatedPerson.getSkillIds());
  }

  @Test
  void updateSkills_returns404NotFoundWhenPersonDoesNotExist() throws Exception {

    IdsRequestDto idsRequest = IdsRequestDto.builder().ids(List.of(1L, 2L)).build();

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/people/999/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idsRequest)))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  void updateProjects_returnsUpdatedPersonAndStatus200OkWhenPersonExists() throws Exception {

    final long EXPECTED_ID = 1L;

    IdsRequestDto idsRequest = IdsRequestDto.builder().ids(List.of(1L, 2L)).build();

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/people/" + EXPECTED_ID + "/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idsRequest)))
        .andExpect(MockMvcResultMatchers.status().isOk());

    PersonEntity updatedPerson =
        personRepository
            .findById(EXPECTED_ID)
            .orElseThrow(() -> new AssertionError("Person Not Found"));

    assertEquals(idsRequest.getIds(), updatedPerson.getProjectIds());
  }

  @Test
  void updateProjects_returns404NotFoundWhenPersonDoesNotExist() throws Exception {

    IdsRequestDto idsRequest = IdsRequestDto.builder().ids(List.of(1L, 2L)).build();

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/people/999/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idsRequest)))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  void getPersonExtended_returnsExtendedPersonByIdAndStatus200Ok() throws Exception {

    final long EXPECTED_ID = 1L;

    PersonEntity person =
        personRepository
            .findById(EXPECTED_ID)
            .orElseThrow(() -> new AssertionError("Person not found: " + EXPECTED_ID));
    person.setProjectIds(new ArrayList<>(List.of(1L)));
    person.setSkillIds(new ArrayList<>(List.of(1L)));
    personRepository.save(person);

    ProjectDto sampleProject = TestDataUtil.sampleProjectDto();
    SkillDto sampleSkill = TestDataUtil.sampleSkillDto();
    when(projectClient.getProjectsByIds(List.of(1L))).thenReturn(List.of(sampleProject));
    when(skillClient.getSkillsByIds(List.of(1L))).thenReturn(List.of(sampleSkill));

    String responseJson =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/people/" + EXPECTED_ID + "/extended")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    PersonExtendedDto fetchedPersonDto =
        objectMapper.readValue(responseJson, PersonExtendedDto.class);

    assertEquals(person.getId(), fetchedPersonDto.getId());
    assertEquals(person.getFirstName(), fetchedPersonDto.getFirstName());
    assertEquals(person.getLastName(), fetchedPersonDto.getLastName());
    assertEquals(List.of(sampleProject), fetchedPersonDto.getProjects());
    assertEquals(List.of(sampleSkill), fetchedPersonDto.getSkills());
  }

  @Test
  void getPersonExtended_returns404NotFoundWhenPersonDoesNotExist() throws Exception {

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/people/999/extended")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  void getPersonExtended_returnsEmptyProjectsAndSkills_whenExternalReturnsEmpty() throws Exception {

    final long EXPECTED_ID = 1L;

    PersonEntity person =
        personRepository
            .findById(EXPECTED_ID)
            .orElseThrow(() -> new AssertionError("Person not found"));

    person.setProjectIds(new ArrayList<>(List.of(1L)));
    person.setSkillIds(new ArrayList<>(List.of(1L)));
    personRepository.save(person);

    when(projectClient.getProjectsByIds(List.of(1L))).thenReturn(List.of());
    when(skillClient.getSkillsByIds(List.of(1L))).thenReturn(List.of());

    String responseJson =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/people/" + EXPECTED_ID + "/extended")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    PersonExtendedDto dto = objectMapper.readValue(responseJson, PersonExtendedDto.class);

    assertEquals(List.of(), dto.getProjects());
    assertEquals(List.of(), dto.getSkills());
  }

  @Test
  void listPeopleExtended_returnsListAndStatus200Ok() throws Exception {

    PersonEntity person = initialPeople.get(0);
    person.setProjectIds(new ArrayList<>(List.of(1L)));
    person.setSkillIds(new ArrayList<>(List.of(1L)));
    personRepository.save(person);

    ProjectDto sampleProject = TestDataUtil.sampleProjectDto();
    SkillDto sampleSkill = TestDataUtil.sampleSkillDto();

    when(projectClient.getProjectsByIds(List.of(1L))).thenReturn(List.of(sampleProject));
    when(skillClient.getSkillsByIds(List.of(1L))).thenReturn(List.of(sampleSkill));

    String responseJson =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/people/extended")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    List<PersonExtendedDto> result =
        objectMapper.readValue(
            responseJson,
            objectMapper
                .getTypeFactory()
                .constructCollectionType(List.class, PersonExtendedDto.class));

    assertEquals(INITIAL_PEOPLE_COUNT, result.size());
  }

  @Test
  void listPeopleExtended_returnsEmptyListWhenNoPeople() throws Exception {

    personRepository.deleteAll();

    String responseJson =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/people/extended")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    List<PersonExtendedDto> result =
        objectMapper.readValue(
            responseJson,
            objectMapper
                .getTypeFactory()
                .constructCollectionType(List.class, PersonExtendedDto.class));

    assertEquals(0, result.size());
  }

  @Test
  void getPeopleByProjectId_returnsMatchingPeople() throws Exception {

    PersonEntity p1 = initialPeople.get(0);
    PersonEntity p2 = initialPeople.get(1);

    p1.setProjectIds(new ArrayList<>(List.of(10L)));
    p2.setProjectIds(new ArrayList<>(List.of(20L)));

    personRepository.saveAll(List.of(p1, p2));

    String responseJson =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/people/by-project/10")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    List<PersonDto> result =
        objectMapper.readValue(
            responseJson,
            objectMapper.getTypeFactory().constructCollectionType(List.class, PersonDto.class));

    assertEquals(1, result.size());
    assertEquals(p1.getId(), result.get(0).getId());
  }

  @Test
  void getPeopleBySkillId_returnsMatchingPeople() throws Exception {

    PersonEntity p1 = initialPeople.get(0);
    PersonEntity p2 = initialPeople.get(1);

    p1.setSkillIds(new ArrayList<>(List.of(10L)));
    p2.setSkillIds(new ArrayList<>(List.of(20L)));

    personRepository.saveAll(List.of(p1, p2));

    String responseJson =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/people/by-skill/10")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    List<PersonDto> result =
        objectMapper.readValue(
            responseJson,
            objectMapper.getTypeFactory().constructCollectionType(List.class, PersonDto.class));

    assertEquals(1, result.size());
    assertEquals(p1.getId(), result.get(0).getId());
  }

  @Test
  void getPeopleByIds_returnsMatchingPeople() throws Exception {

    Long id1 = initialPeople.get(0).getId();
    Long id2 = initialPeople.get(1).getId();

    String responseJson =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/people/by-ids")
                    .param("ids", id1.toString(), id2.toString())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    List<PersonDto> result =
        objectMapper.readValue(
            responseJson,
            objectMapper.getTypeFactory().constructCollectionType(List.class, PersonDto.class));

    assertEquals(2, result.size());
  }
}
