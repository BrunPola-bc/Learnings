package com.brunpola.cv_management.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

  @GetMapping(path = "/home")
  public String homeHello() {
    return "Hello from HOME of CV Management Application!";
  }

  @GetMapping(path = "/")
  public String baseHello() {
    return "Hello from CV Management Application!";
  }

  // @GetMapping("/error")
  // public String errorHello() {
  //   return "Hello from ERROR page of CV Management Application!";
  // }
}
