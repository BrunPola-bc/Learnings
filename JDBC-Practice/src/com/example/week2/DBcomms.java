package com.example.week2;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBcomms {

    String user;
    String password;
    // Connection con;

    public DBcomms(String user, String password) {
        this.user = user;
        this.password = password;
        System.out.println("DBcomms user set to: " + user);
    }

    Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/week1database",
            user,
            password
        );
    }

    // SELECT * FROM tableName;
    void selectAll(String tableName) {
        if(!MyUtils.isSafeIdentifier(tableName)){
            return;
        }
        String query = "SELECT * FROM " + tableName + ";";

        try (Connection con = getConnection();
             Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            MyUtils.showResultSet(rs);  
        }
        catch (SQLException e) {
            if(e.getMessage().contains("doesn't exist")){
                System.out.println("** TABLE '" + tableName + "' DOESN'T EXIST! **");
            }
            else{
                MyUtils.myExceptionHandler(e);
            }
        }
    }

    private void peopleLinkedBy(String linkTable, String linkColumn, String name,
                        String junctionTable, String junctionColumn) {
        int linkId = getIdByName(linkTable, linkColumn, name);
        if(linkId == -1){
            return;
        }
        if( !MyUtils.isSafeIdentifier(junctionTable) || !MyUtils.isSafeIdentifier(junctionColumn) ){
            return;
        }

        String query = """
            SELECT p.FirstName, p.LastName
            FROM People p
            JOIN %s j ON p.ID = j.PersonID
            WHERE j.%s = ?
            """.formatted(junctionTable, junctionColumn);

        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, linkId);
            try (ResultSet rs = stmt.executeQuery()) {
                MyUtils.showResultSet(rs);
            }
        }
        catch (SQLException e) {
            MyUtils.myExceptionHandler(e);
        }
    }

    void peopleWorkingOnProject(String projectName) {

        peopleLinkedBy("Projects", "ProjectName", projectName,
                    "PersonProjects", "ProjectID");
    }

    void peopleWithSkill(String skillName) {

        peopleLinkedBy("Skills", "SkillName", skillName,
                    "PersonSkills", "SkillID");
    }

    private int getProjectIdByName(String projectName) {
        return getIdByName("Projects", "ProjectName", projectName);
    }
    
    private int getSkillIdByName(String skillName) {
        return getIdByName("Skills", "SkillName", skillName);
    }

    private int getIdByName(String tableName, String columnName, String Name){
        if( !MyUtils.isSafeIdentifier(tableName) || !MyUtils.isSafeIdentifier(columnName) ){
            return -1;
        }
        String query = "SELECT ID FROM " + tableName + " WHERE " + columnName + " = ?;";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, Name);
            
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()){
                    return rs.getInt("ID");
                }
                else{
                    System.out.println("** " + tableName.toUpperCase() + " '" + Name + "' NOT FOUND! **");
                    return -1;
                }
            }
        }
        catch (SQLException e) {
            MyUtils.myExceptionHandler(e);
            return -1;
        }
    }

    // This function uses a stored procedure I wrote in Week1.sql
    // but the procedure doesn't differentiate between search term matching a skill or project
    // and it returns a person twice if it matches both
    public void searchPeople1(String searchTerm) {
        try(Connection con = getConnection();
            CallableStatement stmt = con.prepareCall("{CALL SearchPeople(?)}")) {

            stmt.setString(1, searchTerm);
            
            try(ResultSet rs = stmt.executeQuery()) {
                MyUtils.showResultSet(rs);
            }
        }
        catch (SQLException e) {
            MyUtils.myExceptionHandler(e);
        }
    }

    // Added distinction between skill and project matches
    // People still show multiple times because it's possible to match multiple skills/projects
    public void searchPeople2(String searchTerm){
        
        searchTerm = "%" + searchTerm + "%";

        String query = """
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

        try(Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            
            try(ResultSet rs = stmt.executeQuery()) {
                MyUtils.showResultSet(rs);
            }
        }
        catch (SQLException e) {
            MyUtils.myExceptionHandler(e);
        }
    }

    // Another option to test out GROUP_CONCAT functionality
    // This way each person should show up only once
    public void searchPeople3(String searchTerm){
        
        searchTerm = "%" + searchTerm + "%";

        String query = """
                SELECT  p.FirstName,
                        p.LastName,
                        GROUP_CONCAT(DISTINCT s.SkillName) AS Matched_Skills,
                        GROUP_CONCAT(DISTINCT pr.ProjectName) AS Matched_Projects
                FROM People p
                LEFT JOIN PersonSkills ps ON p.ID = ps.PersonID
                LEFT JOIN Skills s  ON ps.SkillID = s.ID
                                    AND s.SkillName LIKE ?
                LEFT JOIN PersonProjects pp ON p.ID = pp.PersonID
                LEFT JOIN Projects pr   ON pp.ProjectID = pr.ID
                                        AND pr.ProjectName LIKE ?
                GROUP BY p.ID
                HAVING Matched_Skills IS NOT NULL
                    OR Matched_Projects IS NOT NULL
                ORDER BY 2,1,3
                """;

        try(Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            
            try(ResultSet rs = stmt.executeQuery()) {
                MyUtils.showResultSet(rs);
            }
        }
        catch (SQLException e) {
            MyUtils.myExceptionHandler(e);
        }
    }
}
