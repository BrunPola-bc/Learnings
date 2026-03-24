package com.brunpola.cv_management.mappers;

/**
 * Generic interface for mapping between an entity and its DTO.
 *
 * @param <ENTITY> the entity type
 * @param <DTO> the data transfer object type
 */
public interface Mapper<ENTITY, DTO> {

  /**
   * Maps the given entity to its corresponding DTO.
   *
   * @param entity the entity to map
   * @return the mapped DTO
   */
  DTO mapTo(ENTITY entity);

  /**
   * Maps the given DTO to its corresponding entity.
   *
   * @param dto the DTO to map
   * @return the mapped entity
   */
  ENTITY mapFrom(DTO dto);
}
