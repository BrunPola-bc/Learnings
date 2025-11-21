package week2.ui;

import java.util.List;

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
      String input = getInput("Enter the number of your choice: ");
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
      String input = getInput("Enter the number of your choice: ");
      if (input.matches("^[0-7]$")) {
        return Integer.parseInt(input);
      } else {
        System.out.println("** INVALID SELECT OPTION INPUT! ** Please try again.");
      }
    }
  }

  // INSERT submenu
  public static int chooseInsertOption() {
    while (true) {
      System.out.println(
          """
          Choose what you want to add:

          \t1 - People (with skills)
                - [also used to ADD NEW SKILLS to an existing person]
          \t2 - Skills
          \t3 - Projects (with required skills and assigned people)
                - [also used to ADD NEW SKILLS and PEOPLE to an existing project]
          \t0 - Back to MAIN MENU
          """);
      String input = getInput("Enter the number of your choice: ");
      if (input.matches("^[0-3]$")) {
        return Integer.parseInt(input);
      } else {
        System.out.println("** INVALID INSERT OPTION INPUT! ** Please try again.");
      }
    }
  }

  // UPDATE submenu
  public static int chooseUpdateOption() {
    while (true) {
      System.out.println(
          """
          What do you want to update?

          \t1 - Persons info
          \t2 - Skill name
          \t3 - Project name
          \t0 - Back to MAIN MENU
          """);
      String input = getInput("Enter the number of your choice: ");
      if (input.matches("^[0-3]$")) {
        return Integer.parseInt(input);
      } else {
        System.out.println("** INVALID UPDATE OPTION INPUT! ** Please try again.");
      }
    }
  }

  // DELETE submenu
  public static int chooseDeleteOption() {
    while (true) {
      System.out.println(
          """
          What do you want to delete?

          \t1 - Person
          \t2 - Person's Skill
          \t3 - Skill
          \t4 - Project
          \t5 - Skills required for a project
          \t6 - People assigned to a project
          \t0 - Back to MAIN MENU
          """);
      String input = getInput("Enter the number of your choice: ");
      if (input.matches("^[0-6]$")) {
        return Integer.parseInt(input);
      } else {
        System.out.println("** INVALID DELETE OPTION INPUT! ** Please try again.");
      }
    }
  }

  public static void hello() {
    System.out.println("Hello from week2!");
  }

  public static void goodbye(String user) {
    System.out.println("Closing app. Goodbye " + user + "!");
  }

  public static void returnToMainMenu() {
    System.out.println("Returning to main menu.");
  }

  public static void notImplementedYet() {
    System.out.println("This feature is not implemented yet.");
  }

  public static void loginSuccess(String user) {
    System.out.println("Login successful! Welcome, " + user + "!");
  }

  public static String getInput(String fmt) {
    return System.console().readLine(fmt);
  }

  // For every other messages
  public static void message(String msg) {
    System.out.println(msg);
  }

  // Not too smart or clean but works for printing lists of model objects
  // (each of them has toString implemented)
  public static <T> void printList(List<T> list, String header) {
    System.out.println("-----------------------");
    System.out.println("\tLIST OUTPUT: " + header);
    if (list == null || list.isEmpty()) {
      System.out.println("(empty list)");
    } else {
      for (T item : list) {
        System.out.println(item);
      }
    }
    System.out.println("-----------------------");
  }

  // Overloaded method without header
  public static <T> void printList(List<T> list) {
    printList(list, "");
  }
}
