package com.brunpola.people_service.config;

import com.brunpola.people_service.client.ProjectHttpClient;
import com.brunpola.people_service.client.SkillHttpClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration(proxyBeanMethods = false)
@ImportHttpServices({ProjectHttpClient.class, SkillHttpClient.class})
public class HttpClientConfig {}
