package com.brunpola.cv_management.dao.impl;

import com.brunpola.cv_management.dao.SkillDao;
import com.brunpola.cv_management.domain.Skill;
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
public class SkillDaoImpl implements SkillDao {

  private final JdbcTemplate jdbcTemplate;

  public SkillDaoImpl(final JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Skill create(Skill skill) {
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps =
              connection.prepareStatement(
                  "INSERT INTO Skills (SkillName) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
          ps.setString(1, skill.getSkillName());
          return ps;
        },
        keyHolder);

    Long generatedId = keyHolder.getKey().longValue();
    skill.setId(generatedId);
    return skill;
  }

  @Override
  public Optional<Skill> findOne(long skillId) {
    List<Skill> results =
        jdbcTemplate.query(
            "SELECT ID, SkillName FROM Skills WHERE ID = ? LIMIT 1;",
            new SkillRowMapper(),
            skillId);

    return results.stream().findFirst();
  }

  public static class SkillRowMapper implements RowMapper<Skill> {

    @Override
    public Skill mapRow(ResultSet rs, int rowNum) throws SQLException {
      return Skill.builder().id(rs.getLong("ID")).skillName(rs.getString("SkillName")).build();
    }
  }

  @Override
  public List<Skill> find() {
    List<Skill> results =
        jdbcTemplate.query("SELECT ID, SkillName FROM Skills;", new SkillRowMapper());
    return results;
  }
}
