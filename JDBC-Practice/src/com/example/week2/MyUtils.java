package com.example.week2;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MyUtils {

    // Restrict stack trace to only my package
    public static void myExceptionHandler(Exception e) {
        String packagePrefix = "com.example";

        System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());

        for (StackTraceElement element : e.getStackTrace()) {
            if (element.getClassName().startsWith(packagePrefix)) {
                System.err.println("\tat " + element.getFileName() + ":" + element.getLineNumber());
            }
        }
    }

    public static void notImplementedYet() {
        System.out.println("This feature is not implemented yet.");
    }

    // Writes ResultSet to console
    // (Formatting needs work)
    public static void showResultSet(ResultSet rs){
        try {
            System.out.println("---- QUERY RESULT ----");
            int columns = rs.getMetaData().getColumnCount();
            for(int i = 1; i <= columns; i++){
                System.out.print(rs.getMetaData().getColumnName(i) + "\t");
            }
            System.out.println();

            if(!rs.next()){
                System.out.println("(empty set)");
                return;
            }

            do{
                for(int i = 1; i <= columns; i++){
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            } while(rs.next());

            System.out.println("----------------------");
        } catch (SQLException e) {
            MyUtils.myExceptionHandler(e);
        }
    }

    // Simple check for SQL injection prevention in table/column names
    public static boolean isSafeIdentifier(String str){
        boolean safe = str.matches("^[a-zA-Z0-9_ ]+$");
        if(!safe){
            System.out.println("** UNSAFE IDENTIFIER INPUT DETECTED! **");
        }
        return safe;
    }
}
