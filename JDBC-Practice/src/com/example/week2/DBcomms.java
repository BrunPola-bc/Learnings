package com.example.week2;

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

    void peopleWorkingOnProject(String projectName) {
        int projectId = getProjectIdByName(projectName);
        if(projectId == -1){
            return;
        }
        String query = """
            SELECT p.FirstName, p.LastName
            FROM People p
            JOIN PersonProjects pp ON p.ID = pp.PersonID
            WHERE pp.ProjectID = ?
            """;
            // Could have another JOIN to Projects to verify project name
            // and wouldn't need the getProjectIdByName function.
            // But I'm not sure how to separate
            //      - project doesn't exist
            //      - project exists but has no people assigned

        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, projectId);
            try (ResultSet rs = stmt.executeQuery()) {
                MyUtils.showResultSet(rs);
            }
        }
        catch (SQLException e) {
            MyUtils.myExceptionHandler(e);
        }
    }

    private int getProjectIdByName(String projectName) {
        String query = "SELECT ID FROM Projects WHERE ProjectName = ?;";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, projectName);
            
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()){
                    return rs.getInt("ID");
                }
                else{
                    System.out.println("** PROJECT '" + projectName + "' NOT FOUND! **");
                    return -1;
                }
            }
        }
        catch (SQLException e) {
            MyUtils.myExceptionHandler(e);
            return -1;
        }
    }

    // For option 1.5 "Show a list of every person with a specific skill"
    // I need getSkillIdByName and peopleWithSkill functions that would be
    // basically identical to the project versions above.
    //
    //      --> Thinking about generalizing these functions to avoid code duplication.
    
    private int getSkillIdByName(String skillName) {
        String query = "SELECT ID FROM Skills WHERE SkillName = ?;";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, skillName);
            
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()){
                    return rs.getInt("ID");
                }
                else{
                    System.out.println("** SKILL '" + skillName + "' NOT FOUND! **");
                    return -1;
                }
            }
        }
        catch (SQLException e) {
            MyUtils.myExceptionHandler(e);
            return -1;
        }
    }
}
