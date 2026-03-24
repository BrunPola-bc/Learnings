package com.brunpola.cv_management.security.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response returned after a successful authentication.
 *
 * <p>Contains the JWT token that should be used for subsequent requests.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

  /** JWT token issued by the authentication system. */
  private String token;
}
