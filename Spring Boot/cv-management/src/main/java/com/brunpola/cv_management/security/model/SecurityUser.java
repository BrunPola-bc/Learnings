package com.brunpola.cv_management.security.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.Collection;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Represents an application-specific user for Spring Security.
 *
 * <p>This entity implements {@link UserDetails} so it can be used by Spring Security for
 * authentication and authorization.
 *
 * <ul>
 *   <li>The {@link #email} field serves as the username.
 *   <li>{@link #roles} are converted to {@link GrantedAuthority} with the "ROLE_" prefix.
 *   <li>Account expiration, locking, and credential checks currently use the default behavior.
 * </ul>
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUser implements UserDetails {

  /** Unique database ID of the user. */
  @Id @GeneratedValue private Long id;

  /** First name of the user. */
  private String firstName;

  /** Last name of the user. */
  private String lastName;

  /** Email of the user, also used as username for authentication. */
  private String email;

  /** Password used for authentication. */
  private String password;

  /**
   * Roles assigned to this user.
   *
   * <p>These are stored as strings in the database and eagerly fetched. They are converted to
   * {@link GrantedAuthority} when required by Spring Security.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  private Set<Role> roles;

  /**
   * Returns the authorities granted to the user.
   *
   * @return a collection of {@link GrantedAuthority} derived from the user's roles {@inheritDoc}
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList();
  }

  /**
   * Returns the username used for authentication.
   *
   * <p>In this implementation, username is the same as the user's email.
   *
   * @return the user's email {@inheritDoc}
   */
  @Override
  public String getUsername() {
    return email;
  }

  /**
   * Returns the password used for authentication.
   *
   * @return the password or {@code null} if not set {@inheritDoc}
   */
  @Override
  public @Nullable String getPassword() {
    return password;
  }

  /**
   * Indicates whether the user's account has expired.
   *
   * <p>Currently uses the default behavior from {@link UserDetails}.
   *
   * @return true if the account is non-expired {@inheritDoc}
   */
  @Override
  public boolean isAccountNonExpired() {
    return UserDetails.super.isAccountNonExpired();
  }

  /**
   * Indicates whether the user's account is locked.
   *
   * <p>Currently uses the default behavior from {@link UserDetails}.
   *
   * @return true if the account is not locked {@inheritDoc}
   */
  @Override
  public boolean isAccountNonLocked() {
    return UserDetails.super.isAccountNonLocked();
  }

  /**
   * Indicates whether the user's credentials have expired.
   *
   * <p>Currently uses the default behavior from {@link UserDetails}.
   *
   * @return true if the credentials are non-expired {@inheritDoc}
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return UserDetails.super.isCredentialsNonExpired();
  }

  /**
   * Indicates whether the user is enabled.
   *
   * <p>Currently uses the default behavior from {@link UserDetails}.
   *
   * @return true if the user is enabled {@inheritDoc}
   */
  @Override
  public boolean isEnabled() {
    return UserDetails.super.isEnabled();
  }
}
