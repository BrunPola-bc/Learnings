package com.brunpola.rest_client_practice;

import com.brunpola.rest_client_practice.clients.UserHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootApplication
public class RestClientPracticeApplication {

  @Bean
  UserHttpClient userHttpClient() {
    RestClient restClient = RestClient.create("https://jsonplaceholder.typicode.com/");
    HttpServiceProxyFactory factory =
        HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
    return factory.createClient(UserHttpClient.class);
  }

  public static void main(String[] args) {
    SpringApplication.run(RestClientPracticeApplication.class, args);
  }
}
