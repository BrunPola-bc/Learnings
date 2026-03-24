package com.brunpola.cv_management.mappers;

/**
 * Generic interface for mapping an entity to a more detailed DTO (extended version).
 *
 * @param <ENTITY> the entity type
 * @param <DTO> the extended DTO type
 */
public interface ExtendedMapper<ENTITY, DTO> {

  /**
   * Maps the given entity to its extended DTO.
   *
   * @param entity the entity to map
   * @return the mapped extended DTO
   */
  DTO mapToExtended(ENTITY entity);
}
