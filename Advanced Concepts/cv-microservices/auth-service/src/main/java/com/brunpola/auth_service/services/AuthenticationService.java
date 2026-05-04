package com.brunpola.auth_service.services;

import com.brunpola.auth_service.model.Role;
import com.brunpola.auth_service.model.SecurityUser;
import com.brunpola.auth_service.model.dto.AuthenticationRequest;
import com.brunpola.auth_service.model.dto.AuthenticationResponse;
import com.brunpola.auth_service.model.dto.RegisterRequest;
import com.brunpola.auth_service.repositories.SecurityUserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final SecurityUserRepository securityUserRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
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

    securityUserRepository.save(user);

    String jwtToken = jwtUtil.generateToken(user);
    return AuthenticationResponse.builder().token(jwtToken).build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {

    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    SecurityUser user = (SecurityUser) authentication.getPrincipal();
    String jwtToken = jwtUtil.generateToken(user);
    return AuthenticationResponse.builder().token(jwtToken).build();
  }
}
