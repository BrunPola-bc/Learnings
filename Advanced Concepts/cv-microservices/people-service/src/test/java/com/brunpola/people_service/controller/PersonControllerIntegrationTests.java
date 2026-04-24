package com.brunpola.people_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.brunpola.people_service.TestDataUtil;
import com.brunpola.people_service.domain.dto.PersonDto;
import com.brunpola.people_service.domain.entity.PersonEntity;
import com.brunpola.people_service.mapper.impl.PersonMapperImpl;
import com.brunpola.people_service.repository.PersonRepository;
import java.util.List;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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
  @Autowired private PersonMapperImpl personMapper;

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
}
