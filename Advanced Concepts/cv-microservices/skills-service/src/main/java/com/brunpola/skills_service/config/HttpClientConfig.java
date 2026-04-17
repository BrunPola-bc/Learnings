package com.brunpola.skills_service.config;

import com.brunpola.skills_service.client.PeopleHttpClient;
import com.brunpola.skills_service.client.ProjectHttpClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration(proxyBeanMethods = false)
@ImportHttpServices({PeopleHttpClient.class, ProjectHttpClient.class})
public class HttpClientConfig {}
