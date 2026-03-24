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

/**
 * Service responsible for user registration and authentication.
 *
 * <p>Handles creating new {@link SecurityUser} accounts, encoding passwords, generating JWT tokens,
 * and authenticating existing users using Spring Security's {@link AuthenticationManager}.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

  /** Repository for persisting and retrieving {@link SecurityUser} entities. */
  private final SecurityUserRepository repository;

  /** Password encoder used to hash user passwords before storage. */
  private final PasswordEncoder passwordEncoder;

  /** Service for generating and validating JWT tokens for authentication. */
  private final JwtService jwtService;

  /** Spring Security authentication manager used to authenticate credentials. */
  private final AuthenticationManager authenticationManager;

  /**
   * Registers a new user with the given {@link RegisterRequest} and {@link Role}.
   *
   * <p>Creates a new {@link SecurityUser}, encodes the password, saves it in the repository, and
   * returns a JWT token for immediate authentication.
   *
   * @param request the registration request containing user details
   * @param role the role to assign to the new user
   * @return an {@link AuthenticationResponse} containing a JWT token
   */
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

  /**
   * Authenticates an existing user with the given {@link AuthenticationRequest}.
   *
   * <p>Validates the provided email and password, retrieves the user, and generates a JWT token
   * upon successful authentication.
   *
   * @param request the authentication request containing email and password
   * @return an {@link AuthenticationResponse} containing a JWT token
   * @throws UsernameNotFoundException if no user exists with the given email
   */
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
