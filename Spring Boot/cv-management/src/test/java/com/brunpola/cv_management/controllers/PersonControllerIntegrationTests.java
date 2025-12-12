package com.brunpola.cv_management.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.brunpola.cv_management.TestDataUtil;
import com.brunpola.cv_management.domain.dto.PersonDto;
import com.brunpola.cv_management.domain.entities.PersonEntity;
import com.brunpola.cv_management.domain.entities.SkillEntity;
import com.brunpola.cv_management.domain.join.PersonSkill;
import com.brunpola.cv_management.domain.join.PersonSkillId;
import com.brunpola.cv_management.exceptions.base.NotFoundException;
import com.brunpola.cv_management.repositories.PersonSkillRepository;
import com.brunpola.cv_management.services.PersonService;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
// @ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class PersonControllerIntegrationTests {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    // private final PersonRepository personRepository;
    private final PersonSkillRepository personSkillRepository;
    private final PersonService personService;

    @Autowired
    public PersonControllerIntegrationTests(
            MockMvc mockMvc,
            // PersonRepository personRepository,
            PersonService personService,
            PersonSkillRepository personSkillRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        // this.personRepository = personRepository;
        this.personService = personService;
        this.personSkillRepository = personSkillRepository;
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
        PersonEntity person1 = personService.findOne(1L);
        PersonEntity person2 = personService.findOne(2L);
        PersonEntity person3 = personService.findOne(3L);

        mockMvc
                .perform(MockMvcRequestBuilders.get("/people").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(person1.getId()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.[0].firstName").value(person1.getFirstName()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.[0].lastName").value(person1.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(person2.getId()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.[1].firstName").value(person2.getFirstName()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.[1].lastName").value(person2.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].id").value(person3.getId()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.[2].firstName").value(person3.getFirstName()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.[2].lastName").value(person3.getLastName()));
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
    public void TestDeletePersonReturnsHttpStatus204NoContentWhenPersonExists() throws Exception {
        PersonEntity personEntityA = TestDataUtil.createTestPersonA();
        PersonEntity saved = personService.save(personEntityA);

        // personService.delete(saved.getId());

        mockMvc
                .perform(
                        MockMvcRequestBuilders.delete("/people/" + saved.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void TestDeletePersonReturnsHttpStatus404NotFoundWhenPersonDoesNotExist()
            throws Exception {

        mockMvc
                .perform(
                        MockMvcRequestBuilders.delete("/people/-1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void TestDeletePersonDeletesPerson() throws Exception {
        PersonEntity personEntityA = TestDataUtil.createTestPersonA();
        PersonEntity saved = personService.save(personEntityA);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.delete("/people/" + saved.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        assertThatThrownBy(() -> personService.findOne(saved.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @Transactional
    public void TestThatDeletePersonServiceCascadeDeletes() throws Exception {
        PersonEntity person = personService.findOne(1L);

        PersonSkill firsPersonSkill = person.getSkills().stream().findFirst().orElseThrow();
        SkillEntity skill = firsPersonSkill.getSkill();

        mockMvc
                .perform(
                        MockMvcRequestBuilders.delete("/people/" + person.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        boolean personSkillExists = personSkillRepository.existsById(new PersonSkillId(person.getId(), skill.getId()));

        assertThat(personSkillExists).isFalse();
    }

    @Test
    public void TestThatListPeopleExtendedReturnsHttpStatus200() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/people/extended").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void TestThatListPeopleExtendedReturnsPeopleWithSkillsAndProjects() throws Exception {

        PersonEntity person = personService.findOne(1L);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/people/extended").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(person.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value(person.getFirstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName").value(person.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].skills").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].projects").isArray());
    }

    @Test
    public void TestThatGetPersonExtendedReturnsHttpStatus200WhenPersonExists() throws Exception {
        PersonEntity person = personService.save(TestDataUtil.createTestPersonA());

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/people/" + person.getId() + "/extended")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void TestThatGetPersonExtendedReturnsCorrectPerson() throws Exception {
        PersonEntity person = personService.save(TestDataUtil.createTestPersonA());

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/people/" + person.getId() + "/extended")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(person.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(person.getFirstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(person.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.skills").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.projects").isArray());
    }

    @Test
    public void TestThatGetPersonExtendedReturnsHttpStatus404WhenPersonDoesNotExist()
            throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/people/-1/extended")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Transactional
    public void TestThatGetPersonExtendedReturnsMappedSkillsAndProjects() throws Exception {
        PersonEntity person = personService.findOne(1L);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/people/" + person.getId() + "/extended")
                                .contentType(MediaType.APPLICATION_JSON))
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
    public void TestThatSearchPeopleByExampleFiltersCorrectly() throws Exception {

        // Arrange: create 3 people
        PersonEntity personA = TestDataUtil.createTestPersonA();
        PersonEntity savedA = personService.save(personA);

        PersonEntity personB = TestDataUtil.createTestPersonB();
        personB.setFirstName("NOTFOUND");
        // PersonEntity savedB =
        personService.save(personB);

        PersonEntity personC = TestDataUtil.createTestPersonA();
        PersonEntity savedC = personService.save(personC);

        // PersonDto searchExample = new PersonDto();
        // searchExample.setFirstName("uno");
        // String searchExampleJson = objectMapper.writeValueAsString(searchExample);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/people/search")
                                .param("firstName", "uno") // works with this if i remove @RequestBody in controller
                                // .param("lastName", "pol")
                                .contentType(MediaType.APPLICATION_JSON)
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
