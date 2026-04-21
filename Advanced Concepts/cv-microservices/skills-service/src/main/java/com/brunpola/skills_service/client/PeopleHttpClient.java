package com.brunpola.skills_service.client;

import com.brunpola.skills_service.domain.external.PersonDto;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "http://localhost:16071/api/people")
public interface PeopleHttpClient {

  @GetExchange("/by-skill/{skillId}")
  List<PersonDto> findPeopleBySkillId(@PathVariable Long skillId);
}
