package com.brunpola.projects_service.client;

import com.brunpola.projects_service.domain.external.SkillDto;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "http://localhost:16073/api/skills")
public interface SkillHttpClient {

  @GetExchange("/{id}")
  SkillDto getSkillById(@PathVariable Long id);

  @GetExchange("/by-ids")
  List<SkillDto> getSkillsByIds(@RequestParam List<Long> ids);
}
