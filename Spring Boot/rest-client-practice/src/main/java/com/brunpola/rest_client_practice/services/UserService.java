package com.brunpola.rest_client_practice.services;

import com.brunpola.rest_client_practice.domain.User;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange(url = "https://jsonplaceholder.typicode.com", accept = "application/json")
public interface UserService {

  @GetExchange("/users")
  List<User> getAllUsers();

  @GetExchange("/users/{id}")
  User getUserById(@PathVariable Long id);

  @PostExchange("/users")
  User createUser(User user);

  @PutExchange("/users/{id}")
  User updateUser(@PathVariable Long id, User user);

  @DeleteExchange("/users/{id}")
  void deleteUser(@PathVariable Long id);
}
