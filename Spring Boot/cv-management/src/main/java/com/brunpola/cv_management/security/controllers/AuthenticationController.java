package com.brunpola.cv_management.security.controllers;

import com.brunpola.cv_management.security.model.Role;
import com.brunpola.cv_management.security.model.dto.AuthenticationRequest;
import com.brunpola.cv_management.security.model.dto.AuthenticationResponse;
import com.brunpola.cv_management.security.model.dto.RegisterRequest;
import com.brunpola.cv_management.security.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller handling authentication and registration endpoints for the CV management system. */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  /** Service handling authentication and registration logic. */
  private final AuthenticationService authService;

  /**
   * Registers a regular user.
   *
   * @param request registration request containing username, password, etc.
   * @return authentication response with JWT token and user info
   */
  @PostMapping("/register-user")
  public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request, Role.USER));
  }

  /**
   * Registers an admin user.
   *
   * @param request registration request containing username, password, etc.
   * @return authentication response with JWT token and user info
   */
  @PostMapping("/register-admin")
  public ResponseEntity<AuthenticationResponse> registerAdmin(
      @RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request, Role.ADMIN));
  }

  /**
   * Registers an extended user.
   *
   * @param request registration request containing username, password, etc.
   * @return authentication response with JWT token and user info
   */
  @PostMapping("/register-extended-user")
  public ResponseEntity<AuthenticationResponse> registerExtendedUser(
      @RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request, Role.EXTENDED_USER));
  }

  /**
   * Authenticates a user with credentials.
   *
   * @param request authentication request containing username and password
   * @return authentication response with JWT token and user info
   */
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(authService.authenticate(request));
  }
}
