package com.brunpola.cv_management.repositories;

import com.brunpola.cv_management.domain.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {}
