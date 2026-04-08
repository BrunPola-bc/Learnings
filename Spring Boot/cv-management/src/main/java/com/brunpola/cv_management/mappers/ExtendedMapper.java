package com.brunpola.cv_management.mappers;

/**
 * Generic interface for mapping an entity to a more detailed DTO (extended version).
 *
 * @param <EntityT> the entity type
 * @param <DtoT> the extended DTO type
 */
public interface ExtendedMapper<EntityT, DtoT> {

  /**
   * Maps the given entity to its extended DTO.
   *
   * @param entity the entity to map
   * @return the mapped extended DTO
   */
  DtoT mapToExtended(EntityT entity);
}
