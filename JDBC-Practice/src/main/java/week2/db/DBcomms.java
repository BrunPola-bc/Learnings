package week2.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import week2.model.Person;
import week2.model.Project;
import week2.model.Skill;
import week2.ui.UI;
import week2.util.MyUtils;

public class DBcomms {

  private final String url;
  private final String user;
  private final String password;

  public String getUser() {
    return user;
  }

  public DBcomms() {
    Properties prop = new Properties();
    try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties"); ) {

      if (input == null) {
        throw new RuntimeException("db.properties not found in resources");
      }
      prop.load(input);

      this.url = prop.getProperty("db.url");
      this.user = prop.getProperty("db.user");
      this.password = prop.getProperty("db.password");

    } catch (IOException e) {
      throw new RuntimeException("Failed to load db.properties", e);
    }
  }

  Connection getConnection() throws SQLException {
    return DriverManager.getConnection(url, user, password);
  }

  // [OLD] - theres new methods below that use RowMapper to avoid code duplication
  public List<Person> selectAllPeople() {
    List<Person> people = new ArrayList<>();
    String query = "SELECT ID, FirstName, LastName FROM People";

    try (Connection con = getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
      while (rs.next()) {
        Person person =
            new Person(rs.getInt("ID"), rs.getString("FirstName"), rs.getString("LastName"));
        people.add(person);
      }
    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }
    return people;
  }

  // [OLD] - theres new methods below that use RowMapper to avoid code duplication
  public List<Skill> selectAllSkills() {
    List<Skill> skills = new ArrayList<>();
    String query = "SELECT ID, SkillName FROM Skills";

    try (Connection con = getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
      while (rs.next()) {
        Skill skill = new Skill(rs.getInt("ID"), rs.getString("SkillName"));
        skills.add(skill);
      }
    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }
    return skills;
  }

  // [OLD] - theres new methods below that use RowMapper to avoid code duplication
  public List<Project> selectAllProjects() {
    List<Project> projects = new ArrayList<>();
    String query = "SELECT ID, ProjectName FROM Projects";

    try (Connection con = getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
      while (rs.next()) {
        Project project = new Project(rs.getInt("ID"), rs.getString("ProjectName"));
        projects.add(project);
      }
    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }
    return projects;
  }

  // Looked into RowMapper pattern to reduce code duplication
  // I guess I got too far out of "Introduction to JDBC" for this week
  private <T> List<T> queryResultList(String sql, RowMapper<T> mapper) {
    List<T> results = new ArrayList<>();

    try (Connection con = getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        results.add(mapper.map(rs));
      }

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }

    return results;
  }

  public List<Person> selectAllPeopleRM() {
    String sql = "SELECT ID, FirstName, LastName FROM People";
    return queryResultList(sql, new PersonMapper());
  }

  public List<Skill> selectAllSkillsRM() {
    String sql = "SELECT ID, SkillName FROM Skills";
    return queryResultList(sql, new SkillMapper());
  }

  public List<Project> selectAllProjectsRM() {
    String sql = "SELECT ID, ProjectName FROM Projects";
    return queryResultList(sql, new ProjectMapper());
  }

  // Generalised method for people linked by project or skill
  private List<Person> peopleLinkedBy(
      String linkTable,
      String linkColumn,
      String name,
      String junctionTable,
      String junctionColumn) {

    List<Person> people = new ArrayList<>();
    int linkId = getIdByName(linkTable, linkColumn, name);

    if (linkId == -1) {
      return null;
    }
    if (!MyUtils.isSafeIdentifier(junctionTable) || !MyUtils.isSafeIdentifier(junctionColumn)) {
      return null;
    }

    String query =
        """
        SELECT p.ID, p.FirstName, p.LastName
        FROM People p
        JOIN %s j ON p.ID = j.PersonID
        WHERE j.%s = ?
        """
            .formatted(junctionTable, junctionColumn);

    try (Connection con = getConnection();
        PreparedStatement stmt = con.prepareStatement(query)) {

      stmt.setInt(1, linkId);
      try (ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
          Person person =
              new Person(rs.getInt("ID"), rs.getString("FirstName"), rs.getString("LastName"));
          people.add(person);
        }

        return people;
      }
    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
      return null;
    }
  }

  public List<Person> peopleWorkingOnProject(String projectName) {

    return peopleLinkedBy("Projects", "ProjectName", projectName, "PersonProjects", "ProjectID");
  }

  public List<Person> peopleWithSkill(String skillName) {

    return peopleLinkedBy("Skills", "SkillName", skillName, "PersonSkills", "SkillID");
  }

  // Generalised from getSkillIdByName and getProjectIdByName
  private int getIdByName(String tableName, String columnName, String Name) {
    if (!MyUtils.isSafeIdentifier(tableName) || !MyUtils.isSafeIdentifier(columnName)) {
      return -1;
    }
    String query = "SELECT ID FROM " + tableName + " WHERE " + columnName + " = ?;";
    try (Connection con = getConnection();
        PreparedStatement stmt = con.prepareStatement(query)) {
      stmt.setString(1, Name);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("ID");
        } else {
          UI.message("** " + tableName.toUpperCase() + " '" + Name + "' NOT FOUND! **");
          return -1;
        }
      }
    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
      return -1;
    }
  }

  // This function uses a stored procedure I wrote in Week1.sql
  // but the procedure doesn't differentiate between search term matching a skill or project
  // and it returns a person twice if it matches both
  public void searchPeople1(String searchTerm) {
    try (Connection con = getConnection();
        CallableStatement stmt = con.prepareCall("{CALL SearchPeople(?)}")) {

      stmt.setString(1, searchTerm);

      try (ResultSet rs = stmt.executeQuery()) {
        MyUtils.showResultSet(rs);
      }
    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }
  }

  // Added distinction between skill and project matches
  // People still show multiple times because it's possible to match multiple skills/projects
  public void searchPeople2(String searchTerm) {

    searchTerm = "%" + searchTerm + "%";

    String query =
        """
        SELECT p.FirstName, p.LastName, "SKILL" AS 'Matched Category', s.SkillName AS 'Search Match'
        FROM People p
        JOIN PersonSkills ps ON p.ID = ps.PersonID
        JOIN Skills s ON ps.SkillID = s.ID
        WHERE s.SkillName LIKE ?
        UNION
        SELECT p.FirstName, p.LastName, "PROJECT", pr.ProjectName
        FROM People p
        JOIN PersonProjects pp ON p.ID = pp.PersonID
        JOIN Projects pr ON pp.ProjectID = pr.ID
        WHERE pr.ProjectName LIKE ?
        ORDER BY 2,1,3
        """;

    try (Connection con = getConnection();
        PreparedStatement stmt = con.prepareStatement(query)) {

      stmt.setString(1, searchTerm);
      stmt.setString(2, searchTerm);

      try (ResultSet rs = stmt.executeQuery()) {
        MyUtils.showResultSet(rs);
      }
    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }
  }

  // Another option to test out GROUP_CONCAT functionality
  // This way each person should show up only once
  // -->  Still not ideal since there's left joins starting from People,
  //      and is filtered by HAVING ... IS NOT NULL
  public void searchPeople3(String searchTerm) {

    searchTerm = "%" + searchTerm + "%";

    String query =
        """
        SELECT  p.FirstName,
                p.LastName,
                GROUP_CONCAT(DISTINCT s.SkillName) AS Matched_Skills,
                GROUP_CONCAT(DISTINCT pr.ProjectName) AS Matched_Projects
        FROM People p
        LEFT JOIN PersonSkills ps ON p.ID = ps.PersonID
        LEFT JOIN Skills s  ON ps.SkillID = s.ID
                            AND s.SkillName LIKE ?
        LEFT JOIN PersonProjects pp ON p.ID = pp.PersonID
        LEFT JOIN Projects pr ON pp.ProjectID = pr.ID
                              AND pr.ProjectName LIKE ?
        GROUP BY p.ID
        HAVING Matched_Skills IS NOT NULL
            OR Matched_Projects IS NOT NULL
        ORDER BY 2,1,3
        """;

    try (Connection con = getConnection();
        PreparedStatement stmt = con.prepareStatement(query)) {

      stmt.setString(1, searchTerm);
      stmt.setString(2, searchTerm);

      try (ResultSet rs = stmt.executeQuery()) {
        MyUtils.showResultSet(rs);
      }
    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }
  }

  // Giving up on the idea of showing what skills/projects matched the search term
  // Just showing people that have either a matching skill or project
  public List<Person> searchPeople4(String searchTerm) {
    List<Person> people = new ArrayList<>();

    searchTerm = "%" + searchTerm + "%";

    String query =
        """
        SELECT p.ID, p.FirstName, p.LastName
        FROM People p
        JOIN PersonSkills ps ON p.ID = ps.PersonID
        JOIN Skills s ON ps.SkillID = s.ID
        WHERE s.SkillName LIKE ?
        UNION
        SELECT p.ID, p.FirstName, p.LastName
        FROM People p
        JOIN PersonProjects pp ON p.ID = pp.PersonID
        JOIN Projects pr ON pp.ProjectID = pr.ID
        WHERE pr.ProjectName LIKE ?
        """;

    try (Connection con = getConnection();
        PreparedStatement stmt = con.prepareStatement(query)) {

      stmt.setString(1, searchTerm);
      stmt.setString(2, searchTerm);

      try (ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
          Person person =
              new Person(rs.getInt("ID"), rs.getString("FirstName"), rs.getString("LastName"));
          people.add(person);
        }

        return people;
      }
    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
      return null;
    }
  }

  // This uses the same query from Week1.sql
  // to filter skills required by a project but not
  // possessed by any person working on it
  public List<Skill> missingSkills(String projectName) {

    List<Skill> missingSkills = new ArrayList<>();

    String query =
        """
        SELECT DISTINCT s.ID, s.SkillName
        FROM Skills s
        JOIN ProjectSkills prs ON s.ID = prs.SkillID
        -- Filter skills by project name
        WHERE prs.ProjectID = (
            SELECT ID FROM Projects
            WHERE ProjectName LIKE ?
        )
        -- Then remove
        AND s.ID NOT IN (
            -- Skills that people working on the project have
            SELECT ps.SkillID
            FROM PersonSkills ps
            JOIN PersonProjects pp ON ps.PersonID = pp.PersonID
            WHERE pp.ProjectID = prs.ProjectID
        )
        """;

    try (Connection con = getConnection();
        PreparedStatement stmt = con.prepareStatement(query)) {

      stmt.setString(1, projectName);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Skill skill = new Skill(rs.getInt("ID"), rs.getString("SkillName"));
          missingSkills.add(skill);
        }

        return missingSkills;
      }
    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
      return null;
    }
  }

  public Person fetchPersonIfExists(Person person) {

    // Assuming FirstName + LastName uniquely identifies a person for simplicity
    // real database would have an email/username/OIB
    String checkSql = "SELECT ID FROM People WHERE FirstName = ? AND LastName = ?;";

    try (Connection con = getConnection();
        PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
      checkStmt.setString(1, person.getFirstName());
      checkStmt.setString(2, person.getLastName());

      try (ResultSet rs = checkStmt.executeQuery()) {
        if (rs.next()) {
          return new Person(rs.getInt("ID"), person.getFirstName(), person.getLastName());
        }
      }

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }
    return null;
  }

  public Person insertNewPerson(Person person) {

    Person existingPerson = fetchPersonIfExists(person);
    if (existingPerson != null) {
      return existingPerson;
    }

    String insertSql = "INSERT INTO People (FirstName, LastName) VALUES (?, ?);";

    try (Connection con = getConnection();
        PreparedStatement insertStmt =
            con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

      insertStmt.setString(1, person.getFirstName());
      insertStmt.setString(2, person.getLastName());

      insertStmt.executeUpdate();
      try (ResultSet keys = insertStmt.getGeneratedKeys()) {
        if (keys.next()) {
          int newId = keys.getInt(1);
          return new Person(newId, person.getFirstName(), person.getLastName());
        }
      }

      throw new RuntimeException("Insert new person succeeded BUT ** no ID obtained **");

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
      return null;
    }
  }

  public Skill fetchSkillIfExists(Skill skill) {

    String checkSql = "SELECT ID FROM Skills WHERE SkillName = ?;";
    try (Connection con = getConnection();
        PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
      checkStmt.setString(1, skill.getSkillName());

      try (ResultSet rs = checkStmt.executeQuery()) {
        if (rs.next()) {
          return new Skill(rs.getInt("ID"), skill.getSkillName());
        }
      }

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }
    return null;
  }

  public Skill insertNewSkill(Skill skill) {

    Skill existingSkill = fetchSkillIfExists(skill);
    if (existingSkill != null) {
      return existingSkill;
    }

    String insertSql = "INSERT INTO Skills (SkillName) VALUES (?);";

    try (Connection con = getConnection();
        PreparedStatement insertStmt =
            con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

      insertStmt.setString(1, skill.getSkillName());

      insertStmt.executeUpdate();
      try (ResultSet keys = insertStmt.getGeneratedKeys()) {
        if (keys.next()) {
          int newId = keys.getInt(1);
          return new Skill(newId, skill.getSkillName());
        }
      }

      throw new RuntimeException("Insert new skill succeeded BUT ** no ID obtained **");

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
      return null;
    }
  }

  public Project fetchProjectIfExists(Project project) {

    String checkSql = "SELECT ID FROM Projects WHERE ProjectName = ?;";
    try (Connection con = getConnection();
        PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
      checkStmt.setString(1, project.getProjectName());

      try (ResultSet rs = checkStmt.executeQuery()) {
        if (rs.next()) {
          return new Project(rs.getInt("ID"), project.getProjectName());
        }
      }

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }
    return null;
  }

  public Project insertNewProject(Project project) {

    Project existingProject = fetchProjectIfExists(project);
    if (existingProject != null) {
      return existingProject;
    }

    String insertSql = "INSERT INTO Projects (ProjectName) VALUES (?);";

    try (Connection con = getConnection();
        PreparedStatement insertStmt =
            con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

      insertStmt.setString(1, project.getProjectName());

      insertStmt.executeUpdate();
      try (ResultSet keys = insertStmt.getGeneratedKeys()) {
        if (keys.next()) {
          int newId = keys.getInt(1);
          return new Project(newId, project.getProjectName());
        }
      }

      throw new RuntimeException("Insert new project succeeded BUT ** no ID obtained **");

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
      return null;
    }
  }

  public boolean linkPersonSkill(Person person, Skill skill) {

    String insertSql = "INSERT IGNORE INTO PersonSkills (PersonID, SkillID) VALUES (?, ?);";

    try (Connection con = getConnection();
        PreparedStatement insertStmt = con.prepareStatement(insertSql)) {

      insertStmt.setInt(1, person.getId());
      insertStmt.setInt(2, skill.getId());

      int affectedRows = insertStmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
      return false;
    }
  }

  public boolean linkProjectSkill(Project project, Skill skill) {

    String insertSql = "INSERT IGNORE INTO ProjectSkills (ProjectID, SkillID) VALUES (?, ?);";

    try (Connection con = getConnection();
        PreparedStatement insertStmt = con.prepareStatement(insertSql)) {

      insertStmt.setInt(1, project.getId());
      insertStmt.setInt(2, skill.getId());

      int affectedRows = insertStmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
      return false;
    }
  }

  public boolean linkPersonProject(Person person, Project project) {

    String insertSql = "INSERT IGNORE INTO PersonProjects (PersonID, ProjectID) VALUES (?, ?);";

    try (Connection con = getConnection();
        PreparedStatement insertStmt = con.prepareStatement(insertSql)) {

      insertStmt.setInt(1, person.getId());
      insertStmt.setInt(2, project.getId());

      int affectedRows = insertStmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
      return false;
    }
  }

  public boolean updatePerson(Person person) {
    String updateSql = "UPDATE People SET FirstName = ?, LastName = ? WHERE ID = ?;";
    try (Connection con = getConnection();
        PreparedStatement updateStmt = con.prepareStatement(updateSql)) {

      updateStmt.setString(1, person.getFirstName());
      updateStmt.setString(2, person.getLastName());
      updateStmt.setInt(3, person.getId());

      int affectedRows = updateStmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
      return false;
    }
  }

  public boolean updateSkill(Skill skill) {
    String updateSql = "UPDATE Skills SET SkillName = ? WHERE ID = ?;";
    try (Connection con = getConnection();
        PreparedStatement updateStmt = con.prepareStatement(updateSql)) {

      updateStmt.setString(1, skill.getSkillName());
      updateStmt.setInt(2, skill.getId());

      int affectedRows = updateStmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
      return false;
    }
  }

  public boolean updateProject(Project project) {
    String updateSql = "UPDATE Projects SET ProjectName = ? WHERE ID = ?;";
    try (Connection con = getConnection();
        PreparedStatement updateStmt = con.prepareStatement(updateSql)) {

      updateStmt.setString(1, project.getProjectName());
      updateStmt.setInt(2, project.getId());

      int affectedRows = updateStmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
      return false;
    }
  }

  public void deletePerson(Person person) {
    String[] deleteSql = {
      "DELETE FROM PersonSkills WHERE PersonID = ?;",
      "DELETE FROM PersonProjects WHERE PersonID = ?;",
      "DELETE FROM People WHERE ID = ?;"
    };

    try (Connection con = getConnection()) {
      for (String sql : deleteSql) {
        try (PreparedStatement deleteStmt = con.prepareStatement(sql)) {
          deleteStmt.setInt(1, person.getId());
          deleteStmt.executeUpdate();
        }
      }

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }
  }

  public void deleteSkill(Skill skill) {
    String[] deleteSql = {
      "DELETE FROM PersonSkills WHERE SkillID = ?;",
      "DELETE FROM ProjectSkills WHERE SkillID = ?;",
      "DELETE FROM Skills WHERE ID = ?;"
    };

    try (Connection con = getConnection()) {
      for (String sql : deleteSql) {
        try (PreparedStatement deleteStmt = con.prepareStatement(sql)) {
          deleteStmt.setInt(1, skill.getId());
          deleteStmt.executeUpdate();
        }
      }

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }
  }

  public void deleteProject(Project project) {
    String[] deleteSql = {
      "DELETE FROM PersonProjects WHERE ProjectID = ?;",
      "DELETE FROM ProjectSkills WHERE ProjectID = ?;",
      "DELETE FROM Projects WHERE ID = ?;"
    };

    try (Connection con = getConnection()) {
      for (String sql : deleteSql) {
        try (PreparedStatement deleteStmt = con.prepareStatement(sql)) {
          deleteStmt.setInt(1, project.getId());
          deleteStmt.executeUpdate();
        }
      }

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }
  }

  public boolean unlinkPersonSkill(Person person, Skill skill) {

    String deleteSql = "DELETE FROM PersonSkills WHERE PersonID = ? AND SkillID = ?;";

    try (Connection con = getConnection();
        PreparedStatement deleteStmt = con.prepareStatement(deleteSql)) {

      deleteStmt.setInt(1, person.getId());
      deleteStmt.setInt(2, skill.getId());

      int affectedRows = deleteStmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
      return false;
    }
  }

  public boolean unlinkPersonProject(Person person, Project project) {

    String deleteSql = "DELETE FROM PersonProjects WHERE PersonID = ? AND ProjectID = ?;";

    try (Connection con = getConnection();
        PreparedStatement deleteStmt = con.prepareStatement(deleteSql)) {

      deleteStmt.setInt(1, person.getId());
      deleteStmt.setInt(2, project.getId());

      int affectedRows = deleteStmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
      return false;
    }
  }

  public boolean unlinkProjectSkill(Project project, Skill skill) {

    String deleteSql = "DELETE FROM ProjectSkills WHERE ProjectID = ? AND SkillID = ?;";

    try (Connection con = getConnection();
        PreparedStatement deleteStmt = con.prepareStatement(deleteSql)) {

      deleteStmt.setInt(1, project.getId());
      deleteStmt.setInt(2, skill.getId());

      int affectedRows = deleteStmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
      return false;
    }
  }
}
