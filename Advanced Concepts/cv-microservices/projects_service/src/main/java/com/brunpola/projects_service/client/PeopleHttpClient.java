package com.brunpola.projects_service.client;

import com.brunpola.projects_service.domain.external.PersonDto;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "http://localhost:16071/api/people")
public interface PeopleHttpClient {

  @GetExchange("/by-project/{projectId}")
  List<PersonDto> findPeopleByProjectId(@PathVariable Long projectId);

  @GetExchange("/by-ids")
  List<PersonDto> getPeopleByIds(@RequestParam List<Long> ids);
}
