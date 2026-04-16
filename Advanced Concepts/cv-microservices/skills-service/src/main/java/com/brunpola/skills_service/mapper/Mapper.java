package com.brunpola.skills_service.mapper;

public interface Mapper<EntityT, DtoT> {

  DtoT mapTo(EntityT entity);

  EntityT mapFrom(DtoT dto);
}
