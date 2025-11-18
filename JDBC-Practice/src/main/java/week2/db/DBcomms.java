package week2.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import week2.util.MyUtils;

public class DBcomms {

  private final String user;
  private final String password;

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }

  // Connection con;

  public DBcomms(String user, String password) {
    this.user = user;
    this.password = password;
    System.out.println("DBcomms user set to: " + user);
  }

  Connection getConnection() throws SQLException {
    return DriverManager.getConnection("jdbc:mysql://localhost:3306/week1database", user, password);
  }

  // SELECT * FROM tableName;
  public void selectAll(String tableName) {
    if (!MyUtils.isSafeIdentifier(tableName)) {
      return;
    }
    String query = "SELECT * FROM " + tableName + ";";

    try (Connection con = getConnection();
        Statement stmt = con.createStatement()) {
      ResultSet rs = stmt.executeQuery(query);
      MyUtils.showResultSet(rs);
    } catch (SQLException e) {
      if (e.getMessage().contains("doesn't exist")) {
        System.out.println("** TABLE '" + tableName + "' DOESN'T EXIST! **");
      } else {
        MyUtils.myExceptionHandler(e);
      }
    }
  }

  private void peopleLinkedBy(
      String linkTable,
      String linkColumn,
      String name,
      String junctionTable,
      String junctionColumn) {
    int linkId = getIdByName(linkTable, linkColumn, name);
    if (linkId == -1) {
      return;
    }
    if (!MyUtils.isSafeIdentifier(junctionTable) || !MyUtils.isSafeIdentifier(junctionColumn)) {
      return;
    }

    String query =
        """
        SELECT p.FirstName, p.LastName
        FROM People p
        JOIN %s j ON p.ID = j.PersonID
        WHERE j.%s = ?
        """
            .formatted(junctionTable, junctionColumn);

    try (Connection con = getConnection();
        PreparedStatement stmt = con.prepareStatement(query)) {

      stmt.setInt(1, linkId);
      try (ResultSet rs = stmt.executeQuery()) {
        MyUtils.showResultSet(rs);
      }
    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }
  }

  public void peopleWorkingOnProject(String projectName) {

    peopleLinkedBy("Projects", "ProjectName", projectName, "PersonProjects", "ProjectID");
  }

  public void peopleWithSkill(String skillName) {

    peopleLinkedBy("Skills", "SkillName", skillName, "PersonSkills", "SkillID");
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
          System.out.println("** " + tableName.toUpperCase() + " '" + Name + "' NOT FOUND! **");
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

  // This uses the same query from Week1.sql
  // to filter skills required by a project but not
  // possessed by any person working on it
  public void missingSkills(String projectName) {

    projectName = "%" + projectName + "%";

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
        MyUtils.showResultSet(rs);
      }
    } catch (SQLException e) {
      MyUtils.myExceptionHandler(e);
    }
  }
}
