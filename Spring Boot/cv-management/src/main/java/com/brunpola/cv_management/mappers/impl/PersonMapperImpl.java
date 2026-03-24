package com.brunpola.cv_management.mappers.impl;

import com.brunpola.cv_management.domain.dto.PersonDto;
import com.brunpola.cv_management.domain.entities.PersonEntity;
import com.brunpola.cv_management.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link Mapper} for {@link PersonEntity} and {@link PersonDto}. Uses {@link
 * ModelMapper} to convert between entity and DTO.
 */
@Component
public class PersonMapperImpl implements Mapper<PersonEntity, PersonDto> {

  private final ModelMapper modelMapper;

  /**
   * Constructs a PersonMapperImpl with the given {@link ModelMapper}.
   *
   * @param modelMapper the model mapper to use for conversions
   */
  public PersonMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  /** {@inheritDoc} */
  @Override
  public PersonDto mapTo(PersonEntity personEntity) {
    return modelMapper.map(personEntity, PersonDto.class);
  }

  /** {@inheritDoc} */
  @Override
  public PersonEntity mapFrom(PersonDto personDto) {
    return modelMapper.map(personDto, PersonEntity.class);
  }
}
