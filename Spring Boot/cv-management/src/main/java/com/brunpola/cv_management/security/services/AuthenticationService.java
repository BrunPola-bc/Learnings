package com.brunpola.cv_management.security.services;

import com.brunpola.cv_management.security.model.Role;
import com.brunpola.cv_management.security.model.SecurityUser;
import com.brunpola.cv_management.security.model.dto.AuthenticationRequest;
import com.brunpola.cv_management.security.model.dto.AuthenticationResponse;
import com.brunpola.cv_management.security.model.dto.RegisterRequest;
import com.brunpola.cv_management.security.repositories.SecurityUserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final SecurityUserRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request, Role role) {
    SecurityUser user =
        SecurityUser.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .roles(Set.of(role))
            .build();
    repository.save(user);

    String jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder().token(jwtToken).build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    SecurityUser user =
        repository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException(request.getEmail()));

    String jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder().token(jwtToken).build();
  }
}
