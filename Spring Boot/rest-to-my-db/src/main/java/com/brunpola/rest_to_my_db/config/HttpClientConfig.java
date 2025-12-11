package com.brunpola.rest_to_my_db.config;

import com.brunpola.rest_to_my_db.services.PersonService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration(proxyBeanMethods = false)
@ImportHttpServices(PersonService.class)
public class HttpClientConfig {}
