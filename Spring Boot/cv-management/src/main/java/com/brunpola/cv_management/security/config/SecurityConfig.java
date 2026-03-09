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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final AuthenticationProvider authenticationProvider;

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
