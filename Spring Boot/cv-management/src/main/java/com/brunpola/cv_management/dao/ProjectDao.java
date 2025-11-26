package com.brunpola.cv_management.dao;

import com.brunpola.cv_management.domain.Project;
import java.util.Optional;

public interface ProjectDao {

  Project create(Project project);

  Optional<Project> findOne(long l);
}
