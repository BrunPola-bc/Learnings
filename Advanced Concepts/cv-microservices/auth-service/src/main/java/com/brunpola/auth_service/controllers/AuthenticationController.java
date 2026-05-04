package com.brunpola.auth_service.controllers;

import com.brunpola.auth_service.model.Role;
import com.brunpola.auth_service.model.dto.AuthenticationRequest;
import com.brunpola.auth_service.model.dto.AuthenticationResponse;
import com.brunpola.auth_service.model.dto.RegisterRequest;
import com.brunpola.auth_service.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authService;

  @PostMapping("/register-user")
  public ResponseEntity<AuthenticationResponse> registerUser(
      @RequestBody @Valid RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request, Role.USER));
  }

  @PostMapping("/register-admin")
  public ResponseEntity<AuthenticationResponse> registerAdmin(
      @RequestBody @Valid RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request, Role.ADMIN));
  }

  @PostMapping("/register-extended-user")
  public ResponseEntity<AuthenticationResponse> registerExtendedUser(
      @RequestBody @Valid RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request, Role.EXTENDED_USER));
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(authService.authenticate(request));
  }
}
