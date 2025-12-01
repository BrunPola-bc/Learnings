package com.brunpola.cv_management.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunpola.cv_management.TestDataUtil;
import com.brunpola.cv_management.domain.entities.SkillEntity;
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
public class SkillControllerIntegrationTests {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;

  @Autowired
  public SkillControllerIntegrationTests(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
    this.objectMapper = new ObjectMapper();
  }

  @Test
  public void TestThatCreateSkillSuccessfullyReturns201Created() throws Exception {

    SkillEntity skill = TestDataUtil.createTestSkill();
    assertThat(skill.getId() == null);
    String skillJson = objectMapper.writeValueAsString(skill);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(skillJson))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  public void TestThatCreateSkillSuccessfullyReturnsSavedSkill() throws Exception {

    SkillEntity skill = TestDataUtil.createTestSkill();
    assertThat(skill.getId() == null);
    String skillJson = objectMapper.writeValueAsString(skill);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(skillJson))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$.skillName").value(skill.getSkillName()));
  }
}
