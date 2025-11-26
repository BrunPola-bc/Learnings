package com.brunpola.cv_management.dao.impl;

import com.brunpola.cv_management.dao.ProjectDao;
import com.brunpola.cv_management.domain.Project;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class ProjectDaoImpl implements ProjectDao {

  private final JdbcTemplate jdbcTemplate;

  public ProjectDaoImpl(final JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Project create(Project project) {
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps =
              connection.prepareStatement(
                  "INSERT INTO Projects (ProjectName) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
          ps.setString(1, project.getProjectName());
          return ps;
        },
        keyHolder);

    Long generatedId = keyHolder.getKey().longValue();
    project.setId(generatedId);
    return project;
  }

  @Override
  public Optional<Project> findOne(long projectId) {
    List<Project> results =
        jdbcTemplate.query(
            "SELECT ID, ProjectName FROM Projects WHERE ID = ? LIMIT 1;",
            new ProjectRowMapper(),
            projectId);

    return results.stream().findFirst();
  }

  public static class ProjectRowMapper implements RowMapper<Project> {

    @Override
    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
      return Project.builder()
          .id(rs.getLong("ID"))
          .projectName(rs.getString("ProjectName"))
          .build();
    }
  }
}
