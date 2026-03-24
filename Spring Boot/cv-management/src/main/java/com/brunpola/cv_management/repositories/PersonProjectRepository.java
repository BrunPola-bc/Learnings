package com.brunpola.cv_management.repositories;

import com.brunpola.cv_management.domain.join.PersonProject;
import com.brunpola.cv_management.domain.join.PersonProjectId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for the join entity {@link PersonProject}. Handles CRUD operations for
 * Person-Project relationships.
 */
@Repository
public interface PersonProjectRepository extends JpaRepository<PersonProject, PersonProjectId> {}
