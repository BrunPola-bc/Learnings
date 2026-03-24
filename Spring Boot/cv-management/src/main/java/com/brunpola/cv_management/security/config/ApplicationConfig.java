package com.brunpola.cv_management.security.config;

import com.brunpola.cv_management.security.services.SecurityUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring configuration class for authentication-related beans.
 *
 * <p>Provides beans for {@link AuthenticationProvider}, {@link PasswordEncoder}, and {@link
 * AuthenticationManager}.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

  /** Service used for loading user details for authentication. */
  private final SecurityUserService securityUserService;

  /**
   * Configures the {@link AuthenticationProvider} to use the {@link SecurityUserService} and {@link
   * PasswordEncoder} for authentication.
   *
   * @return a configured {@link DaoAuthenticationProvider}
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(securityUserService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  /**
   * Provides a {@link PasswordEncoder} bean for encoding and verifying passwords.
   *
   * @return a {@link BCryptPasswordEncoder} instance
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Provides the {@link AuthenticationManager} bean used by Spring Security.
   *
   * @param config the authentication configuration
   * @return the authentication manager
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
    return config.getAuthenticationManager();
  }
}
