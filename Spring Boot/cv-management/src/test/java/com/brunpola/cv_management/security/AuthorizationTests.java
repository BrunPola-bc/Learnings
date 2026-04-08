package com.brunpola.cv_management.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import com.brunpola.cv_management.TestAuthUtil;
import com.brunpola.cv_management.config.TestAuthUtilConfig;
import com.brunpola.cv_management.security.model.Role;
import com.brunpola.cv_management.security.services.AuthenticationService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
// @ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Import(TestAuthUtilConfig.class)
class AuthorizationTests {

  private final MockMvc mockMvc;
  private final TestAuthUtil testAuthUtil;
  public final AuthenticationService authService;

  @Autowired
  public AuthorizationTests(
      MockMvc mockMvc, TestAuthUtil testAuthUtil, AuthenticationService authService) {
    this.mockMvc = mockMvc;
    this.testAuthUtil = testAuthUtil;
    this.authService = authService;
  }

  private String jwtToken;

  @BeforeEach
  void setUp() throws Exception {
    jwtToken = testAuthUtil.registerUser(Role.ADMIN);
  }

  @Test
  void TestThatAccessIsDeniedWithoutAuthorizationHeader() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/people"))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  void TestThatAccessIsDeniedWithInvalidHeaderValuePrefix() throws Exception {
    final String WRONG_HEADER_VALUE_PREFIX = "Wrong start";
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people")
                .header("Authorization", WRONG_HEADER_VALUE_PREFIX + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  void TestThatAccessIsDeniedWithInvalidToken() throws Exception {
    final String INVALID_TOKEN = jwtToken + "corruption";
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people")
                .header("Authorization", "Bearer " + INVALID_TOKEN))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  void TestThatAccessIsDeniedToUsersWithNoUsername() throws Exception {
    jwtToken = testAuthUtil.registerUserWithNoUsername(Role.USER);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people").header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  void TestThatAccessIsDeniedToUsersWithExpiredToken() throws Exception {
    jwtToken = testAuthUtil.generateExpiredToken();

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people").header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  void TestThatAuthenticationIsSkippedIfAlreadyAuthenticated() throws Exception {

    jwtToken = testAuthUtil.generateExpiredToken();

    UsernamePasswordAuthenticationToken existingAuth =
        new UsernamePasswordAuthenticationToken(null, null, List.of());

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people")
                .with(authentication(existingAuth))
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void QuickTest() throws Exception {

    jwtToken = testAuthUtil.generateExpiredToken();

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/people")
                .with(authentication(null))
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }
}
