package week2.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import week2.model.Person;

public class PersonMapper implements RowMapper<Person> {
  @Override
  public Person map(ResultSet rs) throws SQLException {
    return new Person(rs.getInt("ID"), rs.getString("FirstName"), rs.getString("LastName"));
  }
}
