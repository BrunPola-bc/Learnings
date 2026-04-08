package com.brunpola.cv_management.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.brunpola.cv_management.TestAuthUtil;
import com.brunpola.cv_management.TestDataUtil;
import com.brunpola.cv_management.config.TestAuthUtilConfig;
import com.brunpola.cv_management.domain.entities.ProjectEntity;
import com.brunpola.cv_management.exceptions.base.NotFoundException;
import com.brunpola.cv_management.security.model.Role;
import com.brunpola.cv_management.services.ProjectService;
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
class ProjectControllerIntegrationTests {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;
  private final ProjectService projectService;
  private final TestAuthUtil testAuthUtil;

  @Autowired
  public ProjectControllerIntegrationTests(
      MockMvc mockMvc, TestAuthUtil testAuthUtil, ProjectService projectService) {
    this.mockMvc = mockMvc;
    this.objectMapper = new ObjectMapper();
    this.testAuthUtil = testAuthUtil;
    this.projectService = projectService;
  }

  private String jwtToken;

  @BeforeEach
  void setUp() throws Exception {
    jwtToken = testAuthUtil.registerUser(Role.ADMIN);
  }

  @Test
  void TestThatCreateProjectSuccessfullyReturns201Created() throws Exception {

    ProjectEntity project = TestDataUtil.createTestProject();
    assertThat(project.getId()).isNull();
    String projectJson = objectMapper.writeValueAsString(project);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .content(projectJson))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void TestThatCreateProjectSuccessfullyReturnsSavedProject() throws Exception {

    ProjectEntity project = TestDataUtil.createTestProject();
    assertThat(project.getId()).isNull();
    String projectJson = objectMapper.writeValueAsString(project);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .content(projectJson))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$.projectName").value(project.getProjectName()));
  }

  @Test
  void TestThatListProjectsReturnsHttpStatus200() throws Exception {

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void TestThatListProjectsReturnsListOfProjects() throws Exception {
    ProjectEntity project1 = projectService.findOne(1L);
    ProjectEntity project2 = projectService.findOne(2L);
    ProjectEntity project3 = projectService.findOne(3L);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(project1.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].projectName").value(project1.getProjectName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(project2.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[1].projectName").value(project2.getProjectName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[2].id").value(project3.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[2].projectName").value(project3.getProjectName()));
  }

  @Test
  void TestDeleteProjectDeletesProject() throws Exception {
    ProjectEntity projectEntityA = TestDataUtil.createTestProject();
    ProjectEntity saved = projectService.save(projectEntityA);
    Long savedId = saved.getId();

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/projects/" + savedId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isNoContent());

    assertThatThrownBy(() -> projectService.findOne(savedId)).isInstanceOf(NotFoundException.class);
  }

  @Test
  void TestDeleteProjectReturnsHttpStatus404NotFoundWhenProjectDoesNotExist() throws Exception {

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/projects/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }
}
