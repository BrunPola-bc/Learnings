package com.brunpola.cv_management.services.impl;

import com.brunpola.cv_management.domain.entities.SkillEntity;
import com.brunpola.cv_management.exceptions.skill.SkillNotFoundException;
import com.brunpola.cv_management.repositories.SkillRepository;
import com.brunpola.cv_management.services.SkillService;
import java.util.List;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;

/** TEST */
@Service
public class SkillServiceImpl implements SkillService {

  private final SkillRepository skillRepository;

  /**
   * TEST
   *
   * @param skillRepository skillRepository
   */
  public SkillServiceImpl(SkillRepository skillRepository) {
    this.skillRepository = skillRepository;
  }

  @Override
  public SkillEntity save(SkillEntity skill) {
    return skillRepository.save(skill);
  }

  @Override
  public List<SkillEntity> findAll() {
    return StreamSupport.stream(skillRepository.findAll().spliterator(), false).toList();
  }

  @Override
  public SkillEntity findOne(long id) {
    return skillRepository.findById(id).orElseThrow(() -> new SkillNotFoundException(id));
  }

  @Override
  public boolean isExists(Long id) {
    return skillRepository.existsById(id);
  }

  @Override
  public void delete(Long id) {
    if (!isExists(id)) {
      throw new SkillNotFoundException(id);
    }
    skillRepository.deleteById(id);
  }
}
