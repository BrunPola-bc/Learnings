package com.brunpola.cv_management.dao.impl;

import com.brunpola.cv_management.dao.ProjectDao;
import org.springframework.jdbc.core.JdbcTemplate;

public class ProjectDaoImpl implements ProjectDao {

  private final JdbcTemplate jdbcTemplate;

  public ProjectDaoImpl(final JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }
}
