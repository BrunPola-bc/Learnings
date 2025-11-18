package week2;

import week2.db.DBcomms;
import week2.ui.UI;
import week2.util.MyUtils;

public class App {

  static DBcomms comms;

  public static void main(String[] args) throws Exception {
    UI.hello();
    comms = new DBcomms("TestingUser", "TestingUserPass");

    int option;
    do {
      option = UI.chooseOption();

      switch (option) {
        // Close app
        case 0 -> UI.goodbye(comms.getUser());

        // Show lists (SELECT)
        case 1 -> handleSelectOption();

        // Add new rows to tables (INSERT)
        case 2 -> MyUtils.notImplementedYet();

        // Update rows in tables (UPDATE)
        case 3 -> MyUtils.notImplementedYet();

        // Remove rows from tables (DELETE)
        case 4 -> MyUtils.notImplementedYet();
      }
    } while (option != 0);
  }

  static void handleSelectOption() {
    int selectOption;
    do {
      selectOption = UI.chooseSelectOption();

      switch (selectOption) {
        // Return to main menu
        case 0 -> UI.returnToMainMenu();

        // Show ALL people, skills, or projects
        case 1 -> comms.selectAll("People");
        case 2 -> comms.selectAll("Skills");
        case 3 -> comms.selectAll("Projects");

        // Show every person working on a specific project
        case 4 -> {
          String projectName = System.console().readLine("Enter the project name: ");
          comms.peopleWorkingOnProject(projectName);
        }

        // Show every person with a specific skill
        case 5 -> {
          String skillName = System.console().readLine("Enter the skill name: ");
          comms.peopleWithSkill(skillName);
        }

        // Search for people by skill or project
        case 6 -> {
          String searchTerm = System.console().readLine("Enter SEARCH TERM: ");
          // Theres 3 versions of this function in DBcomms.java
          comms.searchPeople3(searchTerm);
        }

        // Show skills missing from a project
        case 7 -> {
          String projectName = System.console().readLine("Enter the project name: ");
          comms.missingSkills(projectName);
        }
      }
    } while (selectOption != 0);
  }
}
