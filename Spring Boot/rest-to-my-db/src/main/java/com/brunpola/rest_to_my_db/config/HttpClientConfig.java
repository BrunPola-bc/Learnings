package com.brunpola.rest_to_my_db.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.brunpola.rest_to_my_db.services.PersonService;

@Configuration(proxyBeanMethods = false)
public class HttpClientConfig {

  @Bean
  public PersonService personService(@Value("${CV_MANAGEMENT_BASE_URL:http://localhost:8080}") String baseUrl) {
    WebClient webClient = WebClient.builder()
        .baseUrl(baseUrl)
        .build();

    WebClientAdapter adapter = WebClientAdapter.create(webClient);

    HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter)
        .build();

    return factory.createClient(PersonService.class);

  }
}
