package com.brunpola.people_service.mapper.impl;

import com.brunpola.people_service.domain.dto.PersonExtendedDto;
import com.brunpola.people_service.domain.entity.PersonEntity;
import com.brunpola.people_service.mapper.ExtendedMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PersonExtendedMapperImpl implements ExtendedMapper<PersonEntity, PersonExtendedDto> {

  private final ModelMapper modelMapper;

  public PersonExtendedMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public PersonExtendedDto mapToExtended(PersonEntity personEntity) {
    return modelMapper.map(personEntity, PersonExtendedDto.class);
  }
}
