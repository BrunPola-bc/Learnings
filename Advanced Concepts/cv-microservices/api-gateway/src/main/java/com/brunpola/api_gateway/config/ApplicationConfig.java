package com.brunpola.api_gateway.config;

// import com.brunpola.api_gateway.services.SecurityUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

  // private final SecurityUserService securityUserService;

  // @Bean
  // public AuthenticationProvider authenticationProvider() {
  //   DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(securityUserService);
  //   authProvider.setPasswordEncoder(passwordEncoder());
  //   return authProvider;
  // }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // @Bean
  // public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
  //   return config.getAuthenticationManager();
  // }
}
