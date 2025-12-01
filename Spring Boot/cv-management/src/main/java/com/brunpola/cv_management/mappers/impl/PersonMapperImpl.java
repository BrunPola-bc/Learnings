package com.brunpola.cv_management.mappers.impl;

import com.brunpola.cv_management.domain.dto.PersonDto;
import com.brunpola.cv_management.domain.entities.PersonEntity;
import com.brunpola.cv_management.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PersonMapperImpl implements Mapper<PersonEntity, PersonDto> {

  private final ModelMapper modelMapper;

  public PersonMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public PersonDto mapTo(PersonEntity personEntity) {
    return modelMapper.map(personEntity, PersonDto.class);
  }

  @Override
  public PersonEntity mapFrom(PersonDto personDto) {
    return modelMapper.map(personDto, PersonEntity.class);
  }
}
