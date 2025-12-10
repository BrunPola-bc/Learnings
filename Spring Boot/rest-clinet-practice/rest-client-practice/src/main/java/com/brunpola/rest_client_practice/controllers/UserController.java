package com.brunpola.rest_client_practice.controllers;

import com.brunpola.rest_client_practice.clients.UserHttpClient;
import com.brunpola.rest_client_practice.clients.UserRestClient;
import com.brunpola.rest_client_practice.domain.User;
import com.brunpola.rest_client_practice.services.UserService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private final UserRestClient userRestClient;
  private final UserHttpClient userHttpClient;
  private final UserService userService;

  public UserController(
      UserRestClient userRestClient, UserHttpClient userHttpClient, UserService userService) {
    this.userRestClient = userRestClient;
    this.userHttpClient = userHttpClient;
    this.userService = userService;
  }

  @GetMapping("restclient/users")
  public List<User> getAllUsers() {
    return userRestClient.findAll();
  }

  @GetMapping("restclient/users/{id}")
  public User getUserById(@PathVariable Integer id) {
    return userRestClient.findById(id);
  }

  @GetMapping("httpclient/users")
  public List<User> getAllUsersHttp() {
    return userHttpClient.findAll();
  }

  @GetMapping("httpclient/users/{id}")
  public User getUserByIdHttp(@PathVariable Integer id) {
    return userHttpClient.findById(id);
  }

  //////////////////////////////////////////////////////////////

  @GetMapping("service/users")
  public List<User> getAllUsersService() {
    return userService.getAllUsers();
  }

  @GetMapping("service/users/{id}")
  public User getUserByIdService(@PathVariable Long id) {
    return userService.getUserById(id);
  }
}
