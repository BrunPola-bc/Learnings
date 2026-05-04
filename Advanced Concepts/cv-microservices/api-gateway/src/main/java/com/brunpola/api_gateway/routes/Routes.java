package com.brunpola.api_gateway.routes;

import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class Routes {

  // Service routes

  @Bean
  public RouterFunction<ServerResponse> peopleServiceRoute() {
    return GatewayRouterFunctions.route("people-service")
        .route(RequestPredicates.path("/api/people/**"), HandlerFunctions.http())
        .before(BeforeFilterFunctions.uri("http://localhost:16071"))
        .build();
  }

  @Bean
  public RouterFunction<ServerResponse> projectsServiceRoute() {
    return GatewayRouterFunctions.route("projects-service")
        .route(RequestPredicates.path("/api/projects/**"), HandlerFunctions.http())
        .before(BeforeFilterFunctions.uri("http://localhost:16072"))
        .build();
  }

  @Bean
  public RouterFunction<ServerResponse> skillsServiceRoute() {
    return GatewayRouterFunctions.route("skills-service")
        .route(RequestPredicates.path("/api/skills/**"), HandlerFunctions.http())
        .before(BeforeFilterFunctions.uri("http://localhost:16073"))
        .build();
  }

  @Bean
  public RouterFunction<ServerResponse> authServiceRoute() {
    return GatewayRouterFunctions.route("auth-service")
        .route(RequestPredicates.path("/api/auth/**"), HandlerFunctions.http())
        .before(BeforeFilterFunctions.uri("http://localhost:16074"))
        .build();
  }

  // Documentation routs

  @Bean
  public RouterFunction<ServerResponse> peopleServiceDocumentationRoute() {
    return GatewayRouterFunctions.route("people-service-documentation")
        .route(
            RequestPredicates.path("/aggregate/people-service/v3/api-docs"),
            HandlerFunctions.http())
        .before(BeforeFilterFunctions.stripPrefix(2))
        .before(BeforeFilterFunctions.uri("http://localhost:16071"))
        .build();
  }

  @Bean
  public RouterFunction<ServerResponse> projectsServiceDocumentationRoute() {
    return GatewayRouterFunctions.route("projects-service-documentation")
        .route(
            RequestPredicates.path("/aggregate/projects-service/v3/api-docs"),
            HandlerFunctions.http())
        .before(BeforeFilterFunctions.stripPrefix(2))
        .before(BeforeFilterFunctions.uri("http://localhost:16072"))
        .build();
  }

  @Bean
  public RouterFunction<ServerResponse> skillsServiceDocumentationRoute() {
    return GatewayRouterFunctions.route("skills-service-documentation")
        .route(
            RequestPredicates.path("/aggregate/skills-service/v3/api-docs"),
            HandlerFunctions.http())
        .before(BeforeFilterFunctions.stripPrefix(2))
        .before(BeforeFilterFunctions.uri("http://localhost:16073"))
        .build();
  }

  @Bean
  public RouterFunction<ServerResponse> authServiceDocumentationRoute() {
    return GatewayRouterFunctions.route("auth-service-documentation")
        .route(
            RequestPredicates.path("/aggregate/auth-service/v3/api-docs"), HandlerFunctions.http())
        .before(BeforeFilterFunctions.stripPrefix(2))
        .before(BeforeFilterFunctions.uri("http://localhost:16074"))
        .build();
  }
}
