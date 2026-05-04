package com.brunpola.skills_service.service;

import com.brunpola.skills_service.domain.dto.SkillDto;
import com.brunpola.skills_service.domain.dto.SkillExtendedDto;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;

public interface SkillService {

  @Validated
  SkillDto save(@Valid SkillDto skill);

  SkillDto update(SkillDto skill);

  List<SkillDto> findAll();

  SkillDto findOne(Long id);

  boolean isExists(Long id);

  SkillDto partialUpdate(Long id, SkillDto skillDto);

  void delete(Long id);

  SkillExtendedDto findOneExtended(Long id, String authHeader);

  List<SkillExtendedDto> findAllExtended(String authHeader);

  List<SkillDto> findByIds(List<Long> ids);
}
