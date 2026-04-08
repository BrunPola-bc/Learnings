package com.brunpola.cv_management.security.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request used for registering a new user or admin.
 *
 * <p>Contains personal information and credentials for account creation.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  /** First name of the user to register. */
  private String firstName;

  /** Last name of the user to register. */
  private String lastName;

  /** Email of the user to register, also used as username. */
  @NotNull private String email;

  /** Password for the new account. */
  private String password;
}
