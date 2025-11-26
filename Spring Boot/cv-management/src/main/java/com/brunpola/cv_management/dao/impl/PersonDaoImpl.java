package com.brunpola.cv_management.dao.impl;

import com.brunpola.cv_management.dao.PersonDao;
import com.brunpola.cv_management.domain.Person;
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
public class PersonDaoImpl implements PersonDao {

  private final JdbcTemplate jdbcTemplate;

  public PersonDaoImpl(final JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  // @Override
  // public void create(Person person) {
  //   jdbcTemplate.update(
  //       "INSERT INTO People (FirstName, LastName) VALUES (?, ?);",
  //       person.getFirstName(),
  //       person.getLastName());
  // }

  @Override
  public Person create(Person person) {
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps =
              connection.prepareStatement(
                  "INSERT INTO People (FirstName, LastName) VALUES (?, ?)",
                  Statement.RETURN_GENERATED_KEYS);
          ps.setString(1, person.getFirstName());
          ps.setString(2, person.getLastName());
          return ps;
        },
        keyHolder);

    Long generatedId = keyHolder.getKey().longValue();
    person.setId(generatedId);
    return person;
  }

  @Override
  public Optional<Person> findOne(long personId) {
    List<Person> results =
        jdbcTemplate.query(
            "SELECT ID, FirstName, LastName FROM People WHERE ID = ? LIMIT 1;",
            new PersonRowMapper(),
            personId);

    return results.stream().findFirst();
  }

  public static class PersonRowMapper implements RowMapper<Person> {

    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
      return Person.builder()
          .id(rs.getLong("ID"))
          .firstName(rs.getString("FirstName"))
          .lastName(rs.getString("LastName"))
          .build();
    }
  }

  @Override
  public List<Person> find() {
    List<Person> results =
        jdbcTemplate.query("SELECT ID, FirstName, LastName FROM People;", new PersonRowMapper());
    return results;
  }
}
