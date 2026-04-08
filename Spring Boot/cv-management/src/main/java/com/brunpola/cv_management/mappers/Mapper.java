package com.brunpola.cv_management.mappers;

/**
 * Generic interface for mapping between an entity and its DTO.
 *
 * @param <EntityT> the entity type
 * @param <DtoT> the data transfer object type
 */
public interface Mapper<EntityT, DtoT> {

  /**
   * Maps the given entity to its corresponding DTO.
   *
   * @param entity the entity to map
   * @return the mapped DTO
   */
  DtoT mapTo(EntityT entity);

  /**
   * Maps the given DTO to its corresponding entity.
   *
   * @param dto the DTO to map
   * @return the mapped entity
   */
  EntityT mapFrom(DtoT dto);
}
