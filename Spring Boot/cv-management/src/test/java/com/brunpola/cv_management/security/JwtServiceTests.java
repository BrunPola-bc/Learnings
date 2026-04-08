package com.brunpola.cv_management.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunpola.cv_management.TestAuthUtil;
import com.brunpola.cv_management.config.TestAuthUtilConfig;
import com.brunpola.cv_management.security.model.Role;
import com.brunpola.cv_management.security.model.SecurityUser;
import com.brunpola.cv_management.security.services.JwtService;
import com.brunpola.cv_management.security.services.SecurityUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
// @ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Import(TestAuthUtilConfig.class)
class JwtServiceTests {

  private static final String WRONG_USERNAME = "wrong@username.com";

  private final TestAuthUtil testAuthUtil;
  private final JwtService jwtService;
  private final SecurityUserService securityUserService;

  @Autowired
  public JwtServiceTests(
      TestAuthUtil testAuthUtil, JwtService jwtService, SecurityUserService securityUserService) {
    this.testAuthUtil = testAuthUtil;
    this.jwtService = jwtService;
    this.securityUserService = securityUserService;
  }

  private String jwtToken;

  @BeforeEach
  void setUp() throws Exception {
    jwtToken = testAuthUtil.registerUser(Role.ADMIN);
  }

  @Test
  void TestThatTokenIsInvalidWhenUsernameDoesNotMatch() {

    String username = jwtService.extractUsername(jwtToken);
    SecurityUser user = securityUserService.loadUserByUsername(username);
    user.setEmail(WRONG_USERNAME);

    assertThat(jwtService.isTokenValid(jwtToken, user)).isFalse();
  }

  @Test
  void TestThatTokenIsInvalidWhenUsernameDoesNotMatchAndIsExpired() {

    String username = jwtService.extractUsername(jwtToken);

    jwtToken = testAuthUtil.generateExpiredToken();

    SecurityUser user = securityUserService.loadUserByUsername(username);

    user.setEmail(WRONG_USERNAME);

    assertThat(jwtService.isTokenValid(jwtToken, user)).isFalse();
  }
}
