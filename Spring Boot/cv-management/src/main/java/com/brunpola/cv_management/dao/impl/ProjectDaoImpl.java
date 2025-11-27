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

  public static class ProjectRowMapper implements RowMapper<Project> {

    @Override
    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
      return Project.builder()
          .id(rs.getLong("ID"))
          .projectName(rs.getString("ProjectName"))
          .build();
    }
  }

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
  public void delete(long projectId) {
    jdbcTemplate.update("DELETE FROM Projects WHERE ID = ?", projectId);
  }

  @Override
  public List<Project> find() {
    List<Project> results =
        jdbcTemplate.query("SELECT ID, ProjectName FROM Projects;", new ProjectRowMapper());
    return results;
  }

  @Override
  public void update(Project project) {
    jdbcTemplate.update(
        "UPDATE Projects SET ProjectName = ? WHERE ID = ?",
        project.getProjectName(),
        project.getId());
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
}
