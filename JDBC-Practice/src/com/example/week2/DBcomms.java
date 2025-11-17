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

    void peopleLinkedBy(String linkTable, String linkColumn, String name,
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
            JOIN %s j pp ON p.ID = j.PersonID
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
}
