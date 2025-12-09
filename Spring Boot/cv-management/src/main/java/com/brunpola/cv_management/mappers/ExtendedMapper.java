package com.brunpola.cv_management.mappers;

public interface ExtendedMapper<ENTITY, DTO> {
  DTO mapToExtended(ENTITY entity);
}
