package week2.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import week2.model.Skill;

public class SkillMapper implements RowMapper<Skill> {
  @Override
  public Skill map(ResultSet rs) throws SQLException {
    return new Skill(rs.getInt("ID"), rs.getString("SkillName"));
  }
}
