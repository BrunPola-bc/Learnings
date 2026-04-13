package com.brunpola.cv_management.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunpola.cv_management.TestAuthUtil;
import com.brunpola.cv_management.config.TestAuthUtilConfig;
import com.brunpola.cv_management.security.model.Role;
import com.brunpola.cv_management.security.model.dto.AuthenticationRequest;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Import(TestAuthUtilConfig.class)
class AuthenticationTests {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;
  private final TestAuthUtil testAuthUtil;

  @Autowired
  public AuthenticationTests(MockMvc mockMvc, TestAuthUtil testAuthUtil) {
    this.mockMvc = mockMvc;
    this.objectMapper = new ObjectMapper();
    this.testAuthUtil = testAuthUtil;
  }

  @Test
  void TestThatRegistersAUser() throws Exception {

    String token = testAuthUtil.registerUser(Role.USER);

    assertThat(token).isNotBlank();
  }

  @Test
  void TestThatRegistersAnExtendedUser() throws Exception {

    String token = testAuthUtil.registerUser(Role.EXTENDED_USER);

    assertThat(token).isNotBlank();
  }

  @Test
  void TestThatRegistersAnAdmin() throws Exception {

    String token = testAuthUtil.registerUser(Role.ADMIN);

    assertThat(token).isNotBlank();
  }

  @Test
  void TestThatAuthenticatesAUser() throws Exception {
    testAuthUtil.registerUser(Role.USER);

    AuthenticationRequest authRequest = TestAuthUtil.createTestAuthenticationRequest();
    String jsonRequest = objectMapper.writeValueAsString(authRequest);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty());
  }

  @Test
  void TestThatAuthenticatesAnExtendedUser() throws Exception {
    testAuthUtil.registerUser(Role.EXTENDED_USER);

    AuthenticationRequest authRequest = TestAuthUtil.createTestAuthenticationRequest();
    String jsonRequest = objectMapper.writeValueAsString(authRequest);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty());
  }

  @Test
  void TestThatAuthenticatesAnAdmin() throws Exception {
    testAuthUtil.registerUser(Role.ADMIN);

    AuthenticationRequest authRequest = TestAuthUtil.createTestAuthenticationRequest();
    String jsonRequest = objectMapper.writeValueAsString(authRequest);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty());
  }

  @Test
  void TestThatInvalidUsernameDoesntGetAuthenticated() throws Exception {
    testAuthUtil.registerUser(Role.ADMIN);

    AuthenticationRequest authRequest = TestAuthUtil.createTestAuthenticationRequest();
    authRequest.setEmail("nonexistant@email.com");
    String jsonRequest = objectMapper.writeValueAsString(authRequest);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
        .andExpect(MockMvcResultMatchers.status().isForbidden())
        .andExpect(MockMvcResultMatchers.jsonPath("$.token").doesNotExist());
  }
}
