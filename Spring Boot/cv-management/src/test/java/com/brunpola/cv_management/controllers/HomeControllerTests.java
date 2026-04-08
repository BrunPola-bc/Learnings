package com.brunpola.cv_management.controllers;

import com.brunpola.cv_management.TestAuthUtil;
import com.brunpola.cv_management.config.TestAuthUtilConfig;
import com.brunpola.cv_management.security.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestAuthUtilConfig.class)
class HomeControllerTests {

  private final MockMvc mockMvc;
  private final TestAuthUtil testAuthUtil;

  @Autowired
  public HomeControllerTests(MockMvc mockMvc, TestAuthUtil testAuthUtil) {
    this.mockMvc = mockMvc;
    this.testAuthUtil = testAuthUtil;
  }

  @Test
  void testHomeEndpoint() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/home"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.content()
                .string("Hello from HOME of CV Management Application!"));
  }

  @Test
  void testBaseEndpoint() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string("Hello from CV Management Application!"));
  }

  @Test
  void testAdminEndpoint_Unauthorized() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/admin"))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
  void testAdminEndpoint_AsAdmin() throws Exception {

    String jwtTokne = testAuthUtil.registerUser(Role.ADMIN);
    final String CORRECT_RESPONSE =
        "Hello from ADMIN page of CV Management Application! This should only be accessible to"
            + " users with ADMIN role.";

    mockMvc
        .perform(MockMvcRequestBuilders.get("/admin").header("Authorization", "Bearer " + jwtTokne))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(CORRECT_RESPONSE));
  }
}
