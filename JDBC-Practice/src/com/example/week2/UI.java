package com.example.week2;

public class UI {

  // Main menu
  public static int chooseOption() {
    while (true) {
      System.out.println(
          """
          Choose what you want to do:

          \t1 - Show list (SELECT)
          \t2 - Add new rows to tables (INSERT)
          \t3 - Update rows in tables (UPDATE)
          \t4 - Remove rows from tables (DELETE)
          \t0 - EXIT APP
          """);
      String input = System.console().readLine("Enter the number of your choice: ");
      if (input.matches("^[0-4]$")) {
        return Integer.parseInt(input);
      } else {
        System.out.println("** INVALID INPUT! ** Please try again.");
      }
    }
  }

  // SELECT submenu
  public static int chooseSelectOption() {
    while (true) {
      System.out.println(
          """
          Choose which list you want to see:

          \t1 - Show all people
          \t2 - Show all skills
          \t3 - Show all projects
          \t4 - Show every person working on a specific project
          \t5 - Show every person with a specific skill
          \t6 - Search for people by skill or project
          \t7 - Show skills missing from a project
          \t0 - Back to MAIN MENU
          """);
      String input = System.console().readLine("Enter the number of your choice: ");
      if (input.matches("^[0-7]$")) {
        return Integer.parseInt(input);
      } else {
        System.out.println("** INVALID SELECT OPTION INPUT! ** Please try again.");
      }
    }
  }
}
