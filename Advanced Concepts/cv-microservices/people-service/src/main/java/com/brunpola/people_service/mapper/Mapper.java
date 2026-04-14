package com.brunpola.people_service.mapper;

public interface Mapper<EntityT, DtoT> {

  DtoT mapTo(EntityT entity);

  EntityT mapFrom(DtoT dto);
}
