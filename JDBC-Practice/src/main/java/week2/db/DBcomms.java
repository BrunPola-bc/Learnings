package week2.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

  public List<Person> selectAllPeople() {
    String sql = "SELECT ID, FirstName, LastName FROM People";
    return queryResultList(sql, new PersonMapper());
  }

  public List<Skill> selectAllSkills() {
    String sql = "SELECT ID, SkillName FROM Skills";
    return queryResultList(sql, new SkillMapper());
  }

  public List<Project> selectAllProjects() {
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

        PersonMapper mapper = new PersonMapper();

        while (rs.next()) {
          Person person = mapper.map(rs);
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

  /* Here are my various attempts at implementing searchPeople functionality
    that shows people along with every matched term in eather skills or projects.
    Unused but kept to show my tought process and query evolution

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
  */

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

        PersonMapper mapper = new PersonMapper();

        while (rs.next()) {
          Person person = mapper.map(rs);
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
        SkillMapper mapper = new SkillMapper();

        while (rs.next()) {
          Skill skill = mapper.map(rs);
          missingSkills.add(skill);
        }

        return missingSkills;
      }
    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
      return null;
    }
  }

  // Generalised fetchIfExists method to reduce code duplication
  //
  // SqlConsumer is a functional interface similar to java.util.function.Consumer
  // but it allows throwing SQLException from the accept method.
  //
  // This makes fetch[Entity]IfExists methods cleaner
  // (setting parameters by Consumer would require try-catch blocks in every lambda)
  public <T> T fetchIfExists(
      String Sql, SqlConsumer<PreparedStatement> paramSetter, RowMapper<T> mapper) {

    try (Connection con = getConnection();
        PreparedStatement stmt = con.prepareStatement(Sql)) {

      paramSetter.accept(stmt);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return mapper.map(rs);
        }
      }

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }
    return null;
  }

  public Person fetchPersonIfExists(Person person) {

    // Assuming FirstName + LastName uniquely identifies a person for simplicity
    // real database would have an email/username/OIB
    return fetchIfExists(
        "SELECT ID, FirstName, LastName FROM People WHERE FirstName = ? AND LastName = ?;",
        stmt -> {
          stmt.setString(1, person.getFirstName());
          stmt.setString(2, person.getLastName());
        },
        new PersonMapper());
  }

  public Skill fetchSkillIfExists(Skill skill) {

    return fetchIfExists(
        "SELECT ID, SkillName FROM Skills WHERE SkillName = ?;",
        stmt -> stmt.setString(1, skill.getSkillName()),
        new SkillMapper());
  }

  public Project fetchProjectIfExists(Project project) {

    return fetchIfExists(
        "SELECT ID, ProjectName FROM Projects WHERE ProjectName = ?;",
        stmt -> stmt.setString(1, project.getProjectName()),
        new ProjectMapper());
  }

  // Generalised insert method that returns generated ID to reduce code duplication
  public Optional<Integer> insertAndReturnId(
      String sql, SqlConsumer<PreparedStatement> paramSetter) {

    try (Connection con = getConnection();
        PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      paramSetter.accept(stmt);
      stmt.executeUpdate();

      try (ResultSet keys = stmt.getGeneratedKeys()) {
        if (keys.next()) {
          return Optional.of(keys.getInt(1));
        }
      }

      throw new RuntimeException("Insert succeeded BUT ** no ID obtained **");

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }

    return Optional.empty();
  }

  public Person insertNewPerson(Person person) {

    Person existing = fetchPersonIfExists(person);
    if (existing != null) return existing;

    Optional<Integer> newId =
        insertAndReturnId(
            "INSERT INTO People (FirstName, LastName) VALUES (?, ?);",
            stmt -> {
              stmt.setString(1, person.getFirstName());
              stmt.setString(2, person.getLastName());
            });

    if (newId.isPresent()) {
      return new Person(newId.get(), person.getFirstName(), person.getLastName());
    }

    return null;
  }

  public Skill insertNewSkill(Skill skill) {

    Skill existing = fetchSkillIfExists(skill);
    if (existing != null) return existing;

    Optional<Integer> newId =
        insertAndReturnId(
            "INSERT INTO Skills (SkillName) VALUES (?);",
            stmt -> stmt.setString(1, skill.getSkillName()));

    if (newId.isPresent()) {
      return new Skill(newId.get(), skill.getSkillName());
    }

    return null;
  }

  public Project insertNewProject(Project project) {

    Project existing = fetchProjectIfExists(project);
    if (existing != null) return existing;

    Optional<Integer> newId =
        insertAndReturnId(
            "INSERT INTO Projects (ProjectName) VALUES (?);",
            stmt -> stmt.setString(1, project.getProjectName()));

    if (newId.isPresent()) {
      return new Project(newId.get(), project.getProjectName());
    }

    return null;
  }

  // Generalised update method to reduce code duplication
  public boolean update(String sql, SqlConsumer<PreparedStatement> paramSetter) {

    try (Connection con = getConnection();
        PreparedStatement updateStmt = con.prepareStatement(sql)) {

      paramSetter.accept(updateStmt);
      return updateStmt.executeUpdate() > 0;

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }
    return false;
  }

  public boolean updatePerson(Person person) {
    return update(
        "UPDATE People SET FirstName = ?, LastName = ? WHERE ID = ?;",
        stmt -> {
          stmt.setString(1, person.getFirstName());
          stmt.setString(2, person.getLastName());
          stmt.setInt(3, person.getId());
        });
  }

  public boolean updateSkill(Skill skill) {

    return update(
        "UPDATE Skills SET SkillName = ? WHERE ID = ?;",
        stmt -> {
          stmt.setString(1, skill.getSkillName());
          stmt.setInt(2, skill.getId());
        });
  }

  public boolean updateProject(Project project) {
    return update(
        "UPDATE Projects SET ProjectName = ? WHERE ID = ?;",
        stmt -> {
          stmt.setString(1, project.getProjectName());
          stmt.setInt(2, project.getId());
        });
  }

  public void deleteEntity(String[] sqls, int id) {

    try (Connection con = getConnection()) {

      con.setAutoCommit(false);
      try {
        for (String sql : sqls) {
          try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
          }
        }
        con.commit();
      } catch (SQLException e) {
        con.rollback();
        throw e;
      } finally {
        con.setAutoCommit(true);
      }

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }
  }

  public void deletePerson(Person person) {
    String[] deleteSql = {
      "DELETE FROM PersonSkills WHERE PersonID = ?;",
      "DELETE FROM PersonProjects WHERE PersonID = ?;",
      "DELETE FROM People WHERE ID = ?;"
    };

    deleteEntity(deleteSql, person.getId());
  }

  public void deleteSkill(Skill skill) {
    String[] deleteSql = {
      "DELETE FROM PersonSkills WHERE SkillID = ?;",
      "DELETE FROM ProjectSkills WHERE SkillID = ?;",
      "DELETE FROM Skills WHERE ID = ?;"
    };

    deleteEntity(deleteSql, skill.getId());
  }

  public void deleteProject(Project project) {
    String[] deleteSql = {
      "DELETE FROM PersonProjects WHERE ProjectID = ?;",
      "DELETE FROM ProjectSkills WHERE ProjectID = ?;",
      "DELETE FROM Projects WHERE ID = ?;"
    };

    deleteEntity(deleteSql, project.getId());
  }

  // Generalised method to link two entities in a junction table
  public boolean linkOrUnlinkEntities(String sql, int id1, int id2) {

    try (Connection con = getConnection();
        PreparedStatement insertStmt = con.prepareStatement(sql)) {

      insertStmt.setInt(1, id1);
      insertStmt.setInt(2, id2);

      int affectedRows = insertStmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
      return false;
    }
  }

  public boolean linkPersonSkill(Person person, Skill skill) {
    return linkOrUnlinkEntities(
        "INSERT IGNORE INTO PersonSkills (PersonID, SkillID) VALUES (?, ?);",
        person.getId(),
        skill.getId());
  }

  public boolean linkProjectSkill(Project project, Skill skill) {
    return linkOrUnlinkEntities(
        "INSERT IGNORE INTO ProjectSkills (ProjectID, SkillID) VALUES (?, ?);",
        project.getId(),
        skill.getId());
  }

  public boolean linkPersonProject(Person person, Project project) {
    return linkOrUnlinkEntities(
        "INSERT IGNORE INTO PersonProjects (PersonID, ProjectID) VALUES (?, ?);",
        person.getId(),
        project.getId());
  }

  public boolean unlinkPersonSkill(Person person, Skill skill) {
    return linkOrUnlinkEntities(
        "DELETE FROM PersonSkills WHERE PersonID = ? AND SkillID = ?;",
        person.getId(),
        skill.getId());
  }

  public boolean unlinkPersonProject(Person person, Project project) {
    return linkOrUnlinkEntities(
        "DELETE FROM PersonProjects WHERE PersonID = ? AND ProjectID = ?;",
        person.getId(),
        project.getId());
  }

  public boolean unlinkProjectSkill(Project project, Skill skill) {
    return linkOrUnlinkEntities(
        "DELETE FROM ProjectSkills WHERE ProjectID = ? AND SkillID = ?;",
        project.getId(),
        skill.getId());
  }
}
