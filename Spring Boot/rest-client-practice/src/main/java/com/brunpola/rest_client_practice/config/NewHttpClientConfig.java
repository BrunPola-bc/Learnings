package com.brunpola.rest_client_practice.config;

import com.brunpola.rest_client_practice.services.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration(proxyBeanMethods = false)
@ImportHttpServices(UserService.class)
public class NewHttpClientConfig {}
