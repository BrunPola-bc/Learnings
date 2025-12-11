package com.brunpola.rest_client_practice.clients;

import com.brunpola.rest_client_practice.domain.User;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserRestClient {

  private final RestClient restClient;

  public UserRestClient(RestClient.Builder builder) {
    this.restClient =
        builder
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .defaultHeader("USERS", "Using Rest Client")
            .build();
  }

  public List<User> findAll() {
    return restClient
        .get()
        .uri("/users")
        .retrieve()
        .body(new ParameterizedTypeReference<List<User>>() {});
  }

  public User findById(Integer id) {
    return restClient.get().uri("/users/{id}", id).retrieve().body(User.class);
  }
}
