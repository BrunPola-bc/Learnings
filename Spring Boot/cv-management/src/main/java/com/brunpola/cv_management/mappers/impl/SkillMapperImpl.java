package com.brunpola.cv_management.mappers.impl;

import com.brunpola.cv_management.domain.dto.SkillDto;
import com.brunpola.cv_management.domain.entities.SkillEntity;
import com.brunpola.cv_management.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class SkillMapperImpl implements Mapper<SkillEntity, SkillDto> {

  private final ModelMapper modelMapper;

  public SkillMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public SkillDto mapTo(SkillEntity skillEntity) {
    return modelMapper.map(skillEntity, SkillDto.class);
  }

  @Override
  public SkillEntity mapFrom(SkillDto skillDto) {
    return modelMapper.map(skillDto, SkillEntity.class);
  }
}
