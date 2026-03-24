package com.brunpola.cv_management.security.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request used for authenticating an existing user.
 *
 * <p>Contains credentials required to obtain an authentication token.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequest {

  /** Email of the user attempting to authenticate. */
  private String email;

  /** Password of the user attempting to authenticate. */
  private String password;
}
