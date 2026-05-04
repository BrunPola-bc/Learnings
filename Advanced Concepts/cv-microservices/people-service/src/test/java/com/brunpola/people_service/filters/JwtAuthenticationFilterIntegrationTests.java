package com.brunpola.people_service.filters;

import com.brunpola.people_service.TestDataUtil;
import com.brunpola.people_service.service.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
class JwtAuthenticationFilterIntegrationTests {

  @Autowired private MockMvc mockMvc;
  @Autowired public JwtUtil jwtUtil;

  private String validToken;
  private UserDetails user;

  @BeforeEach
  void setUp() {
    user = TestDataUtil.dummyUser();
    validToken = "Bearer " + jwtUtil.generateToken(user);
  }

  @Test
  void contextLoads() {}

  @Test
  void requestWithoutToken_shouldReturn403Forbidden() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/people"))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  void tokenNotStartingWithBearer_shouldReturn403Forbidden() throws Exception {

    String invalidToken = "invalid.token.here";
    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/people").header("Authorization", invalidToken))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  void invalidToken_shouldReturn401Unauthorized() throws Exception {

    String invalidToken = "Bearer invalid.token.here";
    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/people").header("Authorization", invalidToken))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  void validToken_shouldReturn200Ok() throws Exception {

    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/people").header("Authorization", validToken))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void expiredToken_shouldReturn401Unauthorized() throws Exception {
    String expiredToken = "Bearer " + TestDataUtil.getExpiredToken(user, jwtUtil);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/people").header("Authorization", expiredToken))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }
}
