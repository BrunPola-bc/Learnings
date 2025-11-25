package com.brunpola.cv_management.dao.impl;

import com.brunpola.cv_management.dao.SkillDao;
import org.springframework.jdbc.core.JdbcTemplate;

public class SkillDaoImpl implements SkillDao {

  private final JdbcTemplate jdbcTemplate;

  public SkillDaoImpl(final JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }
}
