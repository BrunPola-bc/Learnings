package com.brunpola.cv_management;

import com.brunpola.cv_management.security.model.Role;
import com.brunpola.cv_management.security.model.SecurityUser;
import com.brunpola.cv_management.security.model.dto.AuthenticationRequest;
import com.brunpola.cv_management.security.model.dto.AuthenticationResponse;
import com.brunpola.cv_management.security.model.dto.RegisterRequest;
import com.brunpola.cv_management.security.repositories.SecurityUserRepository;
import com.brunpola.cv_management.security.services.AuthenticationService;
import com.brunpola.cv_management.security.services.JwtService;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

public class TestAuthUtil {

  private static final String TEST_USER_FIRST_NAME = "Test";
  private static final String TEST_USER_LAST_NAME = "User";
  private static final String TEST_USER_EMAIL = "testuser@gmail.com";
  private static final String TEST_USER_PASSWORD = "password";

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;
  private final AuthenticationService authService;
  private final AuthenticationManager authenticationManager;
  private final SecurityUserRepository securityUserRepository;
  private final JwtService jwtService;

  @Autowired
  public TestAuthUtil(
      MockMvc mockMvc,
      AuthenticationService authService,
      AuthenticationManager authenticationManager,
      SecurityUserRepository securityUserRepository,
      JwtService jwtService) {
    this.mockMvc = mockMvc;
    this.objectMapper = new ObjectMapper();
    this.authService = authService;
    this.authenticationManager = authenticationManager;
    this.securityUserRepository = securityUserRepository;
    this.jwtService = jwtService;
  }

  public static RegisterRequest createTestRegisterRequest() {
    return new RegisterRequest(
        TEST_USER_FIRST_NAME, TEST_USER_LAST_NAME, TEST_USER_EMAIL, TEST_USER_PASSWORD);
  }

  public static AuthenticationRequest createTestAuthenticationRequest() {
    return new AuthenticationRequest(TEST_USER_EMAIL, TEST_USER_PASSWORD);
  }

  public String registerUser(Role role) throws Exception {

    String endpoint =
        switch (role) {
          case ADMIN -> "/auth/register-admin";
          case EXTENDED_USER -> "/auth/register-extended-user";
          case USER -> "/auth/register-user";
        };

    RegisterRequest request = createTestRegisterRequest();
    String jsonRequest = objectMapper.writeValueAsString(request);

    MvcResult response =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    String responseBody = response.getResponse().getContentAsString();

    AuthenticationResponse authResponse =
        objectMapper.readValue(responseBody, AuthenticationResponse.class);

    return authResponse.getToken();
  }

  /**
   * Register user with no username (email) by bypassing the controllers argument validation
   *
   * @param role the role assigned to the user
   * @return the JWT token for the registered user
   */
  public String registerUserWithNoUsername(Role role) {

    RegisterRequest request =
        new RegisterRequest(TEST_USER_FIRST_NAME, TEST_USER_LAST_NAME, null, TEST_USER_PASSWORD);
    AuthenticationResponse response = authService.register(request, role);
    return response.getToken();
  }

  public String generateExpiredToken() {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(TEST_USER_EMAIL, TEST_USER_PASSWORD));

    SecurityUser user =
        securityUserRepository
            .findByEmail(TEST_USER_EMAIL)
            .orElseThrow(() -> new UsernameNotFoundException(TEST_USER_EMAIL));

    JwtBuilder builder =
        Jwts.builder()
            .subject(user.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() - 1)) // Time in past
            .signWith(jwtService.getSigningKey(), Jwts.SIG.HS256);

    return builder.compact();
  }
}
