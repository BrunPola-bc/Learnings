package com.brunpola.people_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.brunpola.people_service.TestDataUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

class JwtUtilTest {

  private JwtUtil jwtUtil = new JwtUtil();

  @Test
  void extractAllClaims_shouldReturnClaims_whenTokenExpired() {

    UserDetails user = TestDataUtil.dummyUser();

    // create expired token
    String token = TestDataUtil.getExpiredToken(user, jwtUtil);

    Claims claims = jwtUtil.extractAllClaims(token);

    assertNotNull(claims);
    assertEquals("test-user", claims.getSubject());
  }

  @Test
  void extractClaim_shouldReturnNull_whenTokenInvalid() {

    String invalidToken = "invalid.token.here";

    String result = jwtUtil.extractClaim(invalidToken, Claims::getSubject);

    assertNull(result);
  }
}
