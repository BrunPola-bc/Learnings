package com.brunpola.cv_management.security.model;

/**
 * Defines roles used for authorization in the security system.
 *
 * <p>Each role represents a set of privileges that can be assigned to a {@link SecurityUser}.
 */
public enum Role {

  /** Standard user with basic access to the application. */
  USER,

  /** Extended user with additional privileges beyond a standard user. */
  EXTENDED_USER,

  /** Administrator with full access and control over the application. */
  ADMIN
}
