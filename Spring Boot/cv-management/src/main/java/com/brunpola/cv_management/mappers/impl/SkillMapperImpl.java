package com.brunpola.cv_management.mappers.impl;

import com.brunpola.cv_management.domain.dto.SkillDto;
import com.brunpola.cv_management.domain.entities.SkillEntity;
import com.brunpola.cv_management.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/** TEST */
@Component
public class SkillMapperImpl implements Mapper<SkillEntity, SkillDto> {

  private final ModelMapper modelMapper;

  /**
   * TEST
   *
   * @param modelMapper modelMapper
   */
  public SkillMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  /** TEST */
  @Override
  public SkillDto mapTo(SkillEntity skillEntity) {
    return modelMapper.map(skillEntity, SkillDto.class);
  }

  /** TEST */
  @Override
  public SkillEntity mapFrom(SkillDto skillDto) {
    return modelMapper.map(skillDto, SkillEntity.class);
  }
}
