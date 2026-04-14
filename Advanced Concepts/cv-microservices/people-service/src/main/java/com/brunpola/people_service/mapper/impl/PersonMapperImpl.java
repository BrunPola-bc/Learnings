package com.brunpola.people_service.mapper.impl;

import com.brunpola.people_service.domain.dto.PersonDto;
import com.brunpola.people_service.domain.entity.PersonEntity;
import com.brunpola.people_service.mapper.Mapper;
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
