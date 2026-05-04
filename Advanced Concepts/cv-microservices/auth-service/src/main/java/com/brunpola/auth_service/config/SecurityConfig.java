package com.brunpola.auth_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private String[] freeResourceUrls = {
    "/swagger-ui.html",
    "/swagger-ui/**",
    "/v3/api-docs/**",
    "/swagger-resources/**",
    "/api-docs/**",
    "/aggregate/**"
  };

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) {

    http.csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth -> {
              auth.requestMatchers(freeResourceUrls).permitAll();
              auth.requestMatchers("/api/auth/**").permitAll();
              auth.anyRequest().authenticated();
            })
        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable());

    return http.build();
  }
}
