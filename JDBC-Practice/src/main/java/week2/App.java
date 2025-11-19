package week2;

import week2.db.DBcomms;
import week2.ui.UI;

public class App {

  static DBcomms comms;

  public static void main(String[] args) throws Exception {
    UI.hello();
    comms = new DBcomms();
    UI.loginSuccess(comms.getUser());

    int option;
    do {
      option = UI.chooseOption();

      switch (option) {
        // Close app
        case 0 -> UI.goodbye(comms.getUser());

        // Show lists (SELECT)
        case 1 -> handleSelectOption();

        // Add new rows to tables (INSERT)
        case 2 -> UI.notImplementedYet();

        // Update rows in tables (UPDATE)
        case 3 -> UI.notImplementedYet();

        // Remove rows from tables (DELETE)
        case 4 -> UI.notImplementedYet();
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
        case 1 -> {
          var people = comms.selectAllPeopleRM();
          UI.printList(people, "All People (with RowMapper)");
        }
        case 2 -> {
          var skills = comms.selectAllSkillsRM();
          UI.printList(skills, "All Skills (with RowMapper)");
        }
        case 3 -> {
          var projects = comms.selectAllProjectsRM();
          UI.printList(projects, "All Projects (with RowMapper)");
        }

        // Show every person working on a specific project
        case 4 -> {
          String projectName = System.console().readLine("Enter the project name: ");
          var peopleWorkingOnProject = comms.peopleWorkingOnProject(projectName);
          UI.printList(peopleWorkingOnProject, "People working on Project: " + projectName);
        }

        // Show every person with a specific skill
        case 5 -> {
          String skillName = System.console().readLine("Enter the skill name: ");
          var peopleWithSkill = comms.peopleWithSkill(skillName);
          UI.printList(peopleWithSkill, "People with Skill: " + skillName);
        }

        // Search for people by skill or project
        case 6 -> {
          String searchTerm = System.console().readLine("Enter SEARCH TERM: ");
          // There are 4 versions of this function in DBcomms.java
          var searchResults = comms.searchPeople4(searchTerm);
          UI.printList(searchResults, "Search results for: " + searchTerm);
        }

        // Show skills missing from a project
        case 7 -> {
          String projectName = System.console().readLine("Enter the project name: ");
          var missingSkills = comms.missingSkills(projectName);
          UI.printList(missingSkills, "Skills missing from Project: " + projectName);
        }
      }
    } while (selectOption != 0);
  }
}
