package com.brunpola.people_service.client;

import com.brunpola.people_service.domain.external.ProjectDto;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "http://localhost:16072/api/projects")
public interface ProjectHttpClient {

  @GetExchange("/{id}")
  ProjectDto getProjectById(
      @PathVariable Long id, @RequestHeader("Authorization") String authHeader);

  @GetExchange("/by-ids")
  List<ProjectDto> getProjectsByIds(
      @RequestParam List<Long> ids, @RequestHeader("Authorization") String authHeader);
}
