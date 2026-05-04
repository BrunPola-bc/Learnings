package com.brunpola.people_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.brunpola.people_service.TestDataUtil;
import com.brunpola.people_service.domain.dto.IdsRequestDto;
import com.brunpola.people_service.domain.dto.PersonDto;
import com.brunpola.people_service.domain.dto.PersonExtendedDto;
import com.brunpola.people_service.domain.entity.PersonEntity;
import com.brunpola.people_service.service.JwtUtil;
import com.brunpola.people_service.service.PersonService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(PersonController.class)
@AutoConfigureMockMvc(addFilters = false)
class PersonControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private PersonService personService;
  @MockitoBean private JwtUtil jwtUtil;
  @Autowired private ObjectMapper objectMapper;

  PersonDto personDtoWithId;
  PersonDto personDtoNoId;
  PersonEntity personEntityWithId;
  PersonEntity personEntityNoId;

  private static final String FAKE_TOKEN = "Bearer fake-token";

  @BeforeEach
  void setUp() {
    personDtoWithId = TestDataUtil.samplePersonDto(true);
    personDtoNoId = TestDataUtil.samplePersonDto(false);
    personEntityWithId = TestDataUtil.samplePersonEntity(true);
    personEntityNoId = TestDataUtil.samplePersonEntity(false);
  }

  @Test
  void createPerson_returnsCreatedPersonAndStatus201Created() throws Exception {

    when(personService.save(any(PersonDto.class))).thenReturn(personDtoWithId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/people")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personDtoNoId)))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(
            MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(personDtoWithId)));

    verify(personService).save(any(PersonDto.class));
  }

  @Test
  void listPeople_returnsListOfPeopleAndStatus200Ok() throws Exception {

    List<PersonDto> samplePeopleDtos = TestDataUtil.samplePeopleDtos(true, 2);

    when(personService.findAll()).thenReturn(samplePeopleDtos);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/people").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.content()
                .json(objectMapper.writeValueAsString(samplePeopleDtos)));

    verify(personService).findAll();
  }

  @Test
  void getPerson_returnsPersonByIdAndStatus200Ok() throws Exception {

    when(personService.findOne(1L)).thenReturn(personDtoWithId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/people/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(personDtoWithId)));

    verify(personService).findOne(1L);
  }

  @Test
  void fullUpdatePerson_returnsUpdatedPersonAndStatus200Ok() throws Exception {

    when(personService.update(personDtoWithId)).thenReturn(personDtoWithId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/people/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personDtoNoId)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(personDtoWithId)));

    verify(personService).update(personDtoWithId);
  }

  @Test
  void partialUpdatePerson_returnsUpdatedPersonAndStatus200Ok() throws Exception {

    when(personService.partialUpdate(1L, personDtoNoId)).thenReturn(personDtoWithId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/people/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personDtoNoId)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(personDtoWithId)));

    verify(personService).partialUpdate(1L, personDtoNoId);
  }

  @Test
  void deletePerson_returnsStatus204NoContent() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/people/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNoContent());

    verify(personService).delete(1L);
  }

  @Test
  void listPeopleExtended_returnsListOfExtendedPeopleAndStatus200Ok() throws Exception {

    List<PersonExtendedDto> extendedPeople = TestDataUtil.samplePeopleExtendedDtos(true, 3);

    when(personService.findAllExtended(FAKE_TOKEN)).thenReturn(extendedPeople);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/people/extended")
                .header("Authorization", FAKE_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(extendedPeople)));

    verify(personService).findAllExtended(FAKE_TOKEN);
  }

  @Test
  void getPersonExtended_returnsExtendedPersonByIdAndStatus200Ok() throws Exception {

    PersonExtendedDto extendedPerson = TestDataUtil.samplePersonExtendedDto(true);

    when(personService.findOneExtended(1L, FAKE_TOKEN)).thenReturn(extendedPerson);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/people/1/extended")
                .header("Authorization", FAKE_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(extendedPerson)));

    verify(personService).findOneExtended(1L, FAKE_TOKEN);
  }

  @Test
  void updateSkills_returnsUpdatedPersonAndStatus200Ok() throws Exception {

    IdsRequestDto idsRequest = IdsRequestDto.builder().ids(List.of(1L, 2L)).build();

    when(personService.updateSkills(eq(1L), any())).thenReturn(personDtoWithId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/people/1/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idsRequest)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(personDtoWithId)));

    verify(personService).updateSkills(eq(1L), any());
  }

  @Test
  void updateProjects_returnsUpdatedPersonAndStatus200Ok() throws Exception {

    IdsRequestDto idsRequest = IdsRequestDto.builder().ids(List.of(1L, 2L)).build();

    when(personService.updateProjects(eq(1L), any())).thenReturn(personDtoWithId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/people/1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idsRequest)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(personDtoWithId)));

    verify(personService).updateProjects(eq(1L), any());
  }

  @Test
  void getPeopleByProjectId_returnsListAndStatus200Ok() throws Exception {

    List<PersonDto> people = TestDataUtil.samplePeopleDtos(true, 2);

    when(personService.findByProjectId(1L)).thenReturn(people);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/people/by-project/1")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(people)));

    verify(personService).findByProjectId(1L);
  }

  @Test
  void getPeopleBySkillId_returnsListAndStatus200Ok() throws Exception {

    List<PersonDto> people = TestDataUtil.samplePeopleDtos(true, 2);

    when(personService.findBySkillId(1L)).thenReturn(people);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/people/by-skill/1")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(people)));

    verify(personService).findBySkillId(1L);
  }

  @Test
  void getPeopleByIds_returnsListAndStatus200Ok() throws Exception {

    List<Long> ids = List.of(1L, 2L, 3L);
    List<PersonDto> people = TestDataUtil.samplePeopleDtos(true, 3);

    when(personService.findByIds(ids)).thenReturn(people);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/people/by-ids")
                .param("ids", "1", "2", "3")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(people)));

    verify(personService).findByIds(ids);
  }
}
