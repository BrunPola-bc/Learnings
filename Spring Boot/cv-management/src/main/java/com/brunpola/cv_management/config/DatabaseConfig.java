package com.brunpola.cv_management.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseConfig {

  // Spring Boot auto-configures a JdbcTemplate from the DataSource automatically,
  // so this manual configuration class is usually unnecessary and can be removed.
  @Bean
  public JdbcTemplate jdbcTemplate(final DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
