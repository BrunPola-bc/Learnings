package com.brunpola.cv_management.repositories;

import com.brunpola.cv_management.domain.entities.ProjectEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** CRUD Repository for {@link ProjectEntity} entities */
@Repository
public interface ProjectRepository extends CrudRepository<ProjectEntity, Long> {}
