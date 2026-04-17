package com.brunpola.people_service.service;

import com.brunpola.people_service.domain.dto.PersonDto;
import com.brunpola.people_service.domain.dto.PersonExtendedDto;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;

public interface PersonService {

  @Validated
  PersonDto save(@Valid PersonDto person);

  PersonDto update(PersonDto person);

  List<PersonDto> findAll();

  PersonDto findOne(Long id);

  boolean isExists(Long id);

  PersonDto partialUpdate(Long id, PersonDto personDto);

  void delete(Long id);

  PersonDto updateProjects(Long id, List<Long> ids);

  PersonDto updateSkills(Long id, List<Long> ids);

  List<PersonDto> findByProjectId(Long projectId);

  List<PersonDto> findBySkillId(Long skillId);

  PersonExtendedDto findOneExtended(Long id);

  List<PersonExtendedDto> findAllExtended();

  List<PersonDto> findByIds(List<Long> ids);
}
