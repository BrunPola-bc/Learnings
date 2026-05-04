package com.brunpola.skills_service.client;

import com.brunpola.skills_service.domain.external.ProjectDto;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "http://localhost:16072/api/projects")
public interface ProjectHttpClient {

  @GetExchange("/by-skill/{skillId}")
  List<ProjectDto> findProjectsBySkillId(
      @PathVariable("skillId") Long skillId, @RequestHeader("Authorization") String authHeader);
}
