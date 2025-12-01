package com.brunpola.cv_management.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunpola.cv_management.TestDataUtil;
import com.brunpola.cv_management.domain.entities.ProjectEntity;
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
public class ProjectControllerIntegrationTests {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;

  @Autowired
  public ProjectControllerIntegrationTests(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
    this.objectMapper = new ObjectMapper();
  }

  @Test
  public void TestThatCreateProjectSuccessfullyReturns201Created() throws Exception {

    ProjectEntity project = TestDataUtil.createTestProject();
    assertThat(project.getId() == null);
    String projectJson = objectMapper.writeValueAsString(project);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectJson))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  public void TestThatCreateProjectSuccessfullyReturnsSavedProject() throws Exception {

    ProjectEntity project = TestDataUtil.createTestProject();
    assertThat(project.getId() == null);
    String projectJson = objectMapper.writeValueAsString(project);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectJson))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$.projectName").value(project.getProjectName()));
  }
}
