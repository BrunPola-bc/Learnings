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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authService;

  @PostMapping("/register-user")
  public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request, Role.USER));
  }

  @PostMapping("/register-admin")
  public ResponseEntity<AuthenticationResponse> registerAdmin(
      @RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request, Role.ADMIN));
  }

  @PostMapping("/register-extended-user")
  public ResponseEntity<AuthenticationResponse> registerExtendedUser(
      @RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request, Role.EXTENDED_USER));
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(authService.authenticate(request));
  }
}
