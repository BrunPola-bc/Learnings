package com.brunpola.rest_client_practice.clients;

import com.brunpola.rest_client_practice.domain.User;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface UserHttpClient {

  @GetExchange("/users")
  List<User> findAll();

  @GetExchange("/users/{id}")
  User findById(@PathVariable Integer id);
}
