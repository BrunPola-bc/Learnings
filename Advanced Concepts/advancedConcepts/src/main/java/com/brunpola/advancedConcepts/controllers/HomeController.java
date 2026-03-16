package com.brunpola.advancedConcepts.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

  @GetMapping("/home")
  public String home() {
    return "Hello, Home!";
  }

  @GetMapping("/")
  public String root() {
    return "Hello, World!";
  }
}
