package com.brunpola.cv_management.security.config;

import com.brunpola.cv_management.security.filters.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration class.
 *
 * <p>Configures HTTP security, including route authorization, session management, authentication
 * provider, and JWT filter integration.
 */
@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

  /** JWT authentication filter that checks tokens on incoming requests. */
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  /** Authentication provider used to validate user credentials. */
  private final AuthenticationProvider authenticationProvider;

  /**
   * Configures the security filter chain.
   *
   * <p>Key configurations:
   *
   * <ul>
   *   <li>Disables CSRF protection
   *   <li>Configures endpoint access rules
   *   <li>Sets stateless session management
   *   <li>Adds JWT authentication filter before the username/password filter
   * </ul>
   *
   * @param http the {@link HttpSecurity} to configure
   * @return the built {@link SecurityFilterChain}
   * @throws Exception if the filter chain cannot be built
   */
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    // Filter chain config
    http.csrf(csrf -> csrf.disable());

    http.authorizeHttpRequests(
        auth -> {
          auth.requestMatchers("/", "/home", "/auth/**").permitAll();
          // auth.requestMatchers("/people", "/people/*").hasAnyRole("USER", "EXTENDED_USER",
          // "ADMIN");
          auth.requestMatchers("/**/extended/*", "/**/extended")
              .hasAnyRole("EXTENDED_USER", "ADMIN");
          auth.requestMatchers("/admin/**").hasRole("ADMIN");
          auth.anyRequest().authenticated();
        });

    http.sessionManagement(
        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.authenticationProvider(authenticationProvider);

    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
