package com.brunpola.cv_management.config;

import com.brunpola.cv_management.TestAuthUtil;
import com.brunpola.cv_management.security.repositories.SecurityUserRepository;
import com.brunpola.cv_management.security.services.AuthenticationService;
import com.brunpola.cv_management.security.services.JwtService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

@TestConfiguration
public class TestAuthUtilConfig {

  @Bean
  public TestAuthUtil testAuthUtil(
      MockMvc mockMvc /* , ObjectMapper objectMapper */,
      AuthenticationService authService,
      AuthenticationManager authenticationManager,
      SecurityUserRepository securityUserRepository,
      JwtService jwtService) {
    return new TestAuthUtil(
        mockMvc /* , objectMapper */,
        authService,
        authenticationManager,
        securityUserRepository,
        jwtService);
  }
}
