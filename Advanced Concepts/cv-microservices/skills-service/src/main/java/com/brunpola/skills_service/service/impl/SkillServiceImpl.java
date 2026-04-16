package com.brunpola.skills_service.service.impl;

import com.brunpola.skills_service.domain.dto.SkillDto;
import com.brunpola.skills_service.domain.entity.SkillEntity;
import com.brunpola.skills_service.exception.SkillNotFoundException;
import com.brunpola.skills_service.mapper.Mapper;
import com.brunpola.skills_service.repository.SkillRepository;
import com.brunpola.skills_service.service.SkillService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SkillServiceImpl implements SkillService {

  private final SkillRepository skillRepository;
  private final Mapper<SkillEntity, SkillDto> skillMapper;

  public SkillServiceImpl(
      SkillRepository skillRepository, Mapper<SkillEntity, SkillDto> skillMapper) {
    this.skillRepository = skillRepository;
    this.skillMapper = skillMapper;
  }

  @Override
  public SkillDto save(SkillDto skillDto) {
    SkillEntity skillEntity = skillMapper.mapFrom(skillDto);
    skillEntity = skillRepository.save(skillEntity);
    return skillMapper.mapTo(skillEntity);
  }

  @Override
  public SkillDto update(SkillDto skillDto) {
    if (!isExists(skillDto.getId())) {
      throw new SkillNotFoundException(skillDto.getId());
    }

    SkillEntity skillEntity = skillMapper.mapFrom(skillDto);
    skillEntity = skillRepository.save(skillEntity);
    return skillMapper.mapTo(skillEntity);
  }

  @Override
  public List<SkillDto> findAll() {
    return skillRepository.findAll().stream().map(skillMapper::mapTo).toList();
  }

  @Override
  public SkillDto findOne(Long id) {
    SkillEntity skillEntity =
        skillRepository.findById(id).orElseThrow(() -> new SkillNotFoundException(id));
    return skillMapper.mapTo(skillEntity);
  }

  @Override
  public boolean isExists(Long id) {
    return skillRepository.existsById(id);
  }

  @Override
  public SkillDto partialUpdate(Long id, SkillDto skillDto) {

    skillDto.setId(id);

    SkillEntity updatedSkillEntity =
        skillRepository
            .findById(id)
            .map(
                existingSkill -> {
                  Optional.ofNullable(skillDto.getSkillName())
                      .ifPresent(existingSkill::setSkillName);
                  return skillRepository.save(existingSkill);
                })
            .orElseThrow(() -> new SkillNotFoundException(id));

    return skillMapper.mapTo(updatedSkillEntity);
  }

  @Override
  public void delete(Long id) {
    if (!isExists(id)) {
      throw new SkillNotFoundException(id);
    }
    skillRepository.deleteById(id);
  }
}
