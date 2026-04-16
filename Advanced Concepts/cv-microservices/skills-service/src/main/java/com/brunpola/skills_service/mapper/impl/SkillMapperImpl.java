package com.brunpola.skills_service.mapper.impl;

import com.brunpola.skills_service.domain.dto.SkillDto;
import com.brunpola.skills_service.domain.entity.SkillEntity;
import com.brunpola.skills_service.mapper.Mapper;
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
