package com.brunpola.cv_management.dao.impl;

import com.brunpola.cv_management.dao.PersonDao;
import org.springframework.jdbc.core.JdbcTemplate;

public class PersonDaoImpl implements PersonDao {

  private final JdbcTemplate jdbcTemplate;

  public PersonDaoImpl(final JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }
}
