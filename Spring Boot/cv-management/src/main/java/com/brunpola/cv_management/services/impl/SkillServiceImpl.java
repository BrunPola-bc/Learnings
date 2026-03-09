package com.brunpola.cv_management.services.impl;

import com.brunpola.cv_management.domain.entities.SkillEntity;
import com.brunpola.cv_management.repositories.SkillRepository;
import com.brunpola.cv_management.services.SkillService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;

@Service
public class SkillServiceImpl implements SkillService {

  private final SkillRepository skillRepository;

  public SkillServiceImpl(SkillRepository skillRepository) {
    this.skillRepository = skillRepository;
  }

  @Override
  public SkillEntity createSkill(SkillEntity skill) {
    return skillRepository.save(skill);
  }

  @Override
  public List<SkillEntity> findAll() {
    return StreamSupport.stream(skillRepository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }
}
