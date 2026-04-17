package com.brunpola.projects_service.config;

import com.brunpola.projects_service.client.PeopleHttpClient;
import com.brunpola.projects_service.client.SkillHttpClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration(proxyBeanMethods = false)
@ImportHttpServices({PeopleHttpClient.class, SkillHttpClient.class})
public class HttpClientConfig {}
