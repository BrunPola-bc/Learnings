package com.brunpola.people_service.mapper;

public interface ExtendedMapper<EntityT, DtoT> {

  DtoT mapToExtended(EntityT entity);
}
