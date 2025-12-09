package com.brunpola.cv_management.mappers;

public interface Mapper<ENTITY, DTO> {

  DTO mapTo(ENTITY entity);

  ENTITY mapFrom(DTO dto);
}
