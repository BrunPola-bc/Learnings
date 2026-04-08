package com.brunpola.cv_management.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for simple, non-functional endpoint that only return a string message. Used for
 * testing
 */
@RestController
public class HomeController {

  /**
   * Endpoint providing only a simple message
   *
   * @return a {@link String} greeting for the /home endpoint
   */
  @GetMapping(path = "/home")
  public String homeHello() {
    return "Hello from HOME of CV Management Application!";
  }

  /**
   * Endpoint providing only a simple message
   *
   * @return a {@link String} greeting for the base (/) endpoint
   */
  @GetMapping(path = "/")
  public String baseHello() {
    return "Hello from CV Management Application!";
  }

  /**
   * Returns a greeting message for /admin endpoint. Intended for use with ADMIN role.
   *
   * @return a {@link String} greeting for the /admin endpoint
   */
  @GetMapping("/admin")
  public String adminHello() {
    return "Hello from ADMIN page of CV Management Application! This should only be accessible to"
        + " users with ADMIN role.";
  }
}
