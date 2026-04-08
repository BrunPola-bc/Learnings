package com.brunpola.cv_management.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.brunpola.cv_management.TestAuthUtil;
import com.brunpola.cv_management.TestDataUtil;
import com.brunpola.cv_management.config.TestAuthUtilConfig;
import com.brunpola.cv_management.domain.entities.SkillEntity;
import com.brunpola.cv_management.exceptions.base.NotFoundException;
import com.brunpola.cv_management.security.model.Role;
import com.brunpola.cv_management.services.SkillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
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
class SkillControllerIntegrationTests {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;
  private final SkillService skillService;
  private final TestAuthUtil testAuthUtil;

  @Autowired
  public SkillControllerIntegrationTests(
      MockMvc mockMvc, TestAuthUtil testAuthUtil, SkillService skillService) {
    this.mockMvc = mockMvc;
    this.skillService = skillService;
    this.objectMapper = new ObjectMapper();
    this.testAuthUtil = testAuthUtil;
  }

  private String jwtToken;

  @BeforeEach
  void setUp() throws Exception {
    jwtToken = testAuthUtil.registerUser(Role.ADMIN);
  }

  @Test
  void TestThatCreateSkillSuccessfullyReturns201Created() throws Exception {

    SkillEntity skill = TestDataUtil.createTestSkill();
    assertThat(skill.getId()).isNull();
    String skillJson = objectMapper.writeValueAsString(skill);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .content(skillJson))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void TestThatCreateSkillSuccessfullyReturnsSavedSkill() throws Exception {

    SkillEntity skill = TestDataUtil.createTestSkill();
    assertThat(skill.getId()).isNull();
    String skillJson = objectMapper.writeValueAsString(skill);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .content(skillJson))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$.skillName").value(skill.getSkillName()));
  }

  @Test
  void TestThatListSkillsReturnsHttpStatus200() throws Exception {

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void TestThatListSkillsReturnsListOfSkills() throws Exception {
    SkillEntity skill1 = skillService.findOne(1L);
    SkillEntity skill2 = skillService.findOne(2L);
    SkillEntity skill3 = skillService.findOne(3L);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(skill1.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].skillName").value(skill1.getSkillName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(skill2.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].skillName").value(skill2.getSkillName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[2].id").value(skill3.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[2].skillName").value(skill3.getSkillName()));
  }

  @Test
  void TestDeleteSkillDeletesSkill() throws Exception {
    SkillEntity skillEntityA = TestDataUtil.createTestSkill();
    SkillEntity saved = skillService.save(skillEntityA);
    Long savedId = saved.getId();

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/skills/" + savedId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isNoContent());

    assertThatThrownBy(() -> skillService.findOne(savedId)).isInstanceOf(NotFoundException.class);
  }

  @Test
  void TestDeleteSkillReturnsHttpStatus404NotFoundWhenSkillDoesNotExist() throws Exception {

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/skills/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }
}
