package week2.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import week2.model.Project;

public class ProjectMapper implements RowMapper<Project> {
  @Override
  public Project map(ResultSet rs) throws SQLException {
    return new Project(rs.getInt("ID"), rs.getString("ProjectName"));
  }
}
