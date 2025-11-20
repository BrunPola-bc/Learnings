package week2;

import week2.db.DBcomms;
import week2.model.Person;
import week2.model.Project;
import week2.model.Skill;
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
        case 2 -> handleInsertOption();

        // Update rows in tables (UPDATE)
        case 3 -> handleUpdateOption();

        // Remove rows from tables (DELETE)
        case 4 -> handleDeleteOption();
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
          String projectName = UI.getInput("Enter the project name: ");
          var peopleWorkingOnProject = comms.peopleWorkingOnProject(projectName);
          UI.printList(peopleWorkingOnProject, "People working on Project: " + projectName);
        }

        // Show every person with a specific skill
        case 5 -> {
          String skillName = UI.getInput("Enter the skill name: ");
          var peopleWithSkill = comms.peopleWithSkill(skillName);
          UI.printList(peopleWithSkill, "People with Skill: " + skillName);
        }

        // Search for people by skill or project
        case 6 -> {
          String searchTerm = UI.getInput("Enter SEARCH TERM: ");
          // There are 4 versions of this function in DBcomms.java
          var searchResults = comms.searchPeople4(searchTerm);
          UI.printList(searchResults, "Search results for: " + searchTerm);
        }

        // Show skills missing from a project
        case 7 -> {
          String projectName = UI.getInput("Enter the project name: ");
          var missingSkills = comms.missingSkills(projectName);
          UI.printList(missingSkills, "Skills missing from Project: " + projectName);
        }
      }
    } while (selectOption != 0);
  }

  static void handleInsertOption() {
    int insertOption;
    do {
      insertOption = UI.chooseInsertOption();

      switch (insertOption) {
        // Return to main menu
        case 0 -> UI.returnToMainMenu();

        // Insert new person (with their skills)
        case 1 -> {
          String firstName = UI.getInput("Enter person's first name: ");
          String lastName = UI.getInput("Enter person's last name: ");
          Person newPerson = new Person(firstName, lastName);
          // Not too user friendly, but I already spent a lot of time outside the scope of JDBC for
          // this learning path
          String skills = UI.getInput("Enter person's (new) skills [comma separated]: ");

          newPerson = comms.insertNewPerson(newPerson);
          String[] skillNames = skills.split(",");
          int createdLinksCount = 0;
          for (String s : skillNames) {
            s = s.trim();
            if (!s.isEmpty()) {
              Skill skill = new Skill(s);
              skill = comms.insertNewSkill(skill);

              if (comms.linkPersonSkill(newPerson, skill)) {
                createdLinksCount++;
              }
            }
          }
          UI.message(
              "Updated "
                  + newPerson.getFirstName()
                  + " "
                  + newPerson.getLastName()
                  + " with "
                  + createdLinksCount
                  + " new skills.");
        }

        // Insert new skill
        case 2 -> {
          String skillName = UI.getInput("Enter skill name: ");
          Skill newSkill = new Skill(skillName);
          comms.insertNewSkill(newSkill);
          UI.message(
              "Success: skill '"
                  + skillName
                  + "' is in the database."); // eather now or from before
        }

        // Insert new project (with its required skills and assigned people)
        case 3 -> {
          String projectName = UI.getInput("Enter project name: ");
          Project newProject = new Project(projectName);
          newProject = comms.insertNewProject(newProject);

          // Not too user friendly, but I already spent a lot of time outside the scope of JDBC
          String skills =
              UI.getInput("Enter (new) skills required for the project [comma separated]: ");
          String[] skillNames = skills.split(",");
          int createdLinksCount = 0;
          for (String s : skillNames) {
            s = s.trim();
            if (!s.isEmpty()) {
              Skill skill = new Skill(s);
              skill = comms.insertNewSkill(skill);

              if (comms.linkProjectSkill(newProject, skill)) {
                createdLinksCount++;
              }
            }
          }
          UI.message(
              "Updated "
                  + createdLinksCount
                  + " skills required for project '"
                  + newProject.getProjectName()
                  + "'.");

          // Not too user friendly, but I already spent a lot of time outside the scope of JDBC
          String people =
              UI.getInput(
                  "Enter (new) people assigned to the project [format: First Last, comma"
                      + " separated]: ");
          String[] personNames = people.split(",");
          createdLinksCount = 0;
          for (String p : personNames) {
            p = p.trim();
            if (!p.isEmpty()) {
              String[] nameParts = p.split(" ");
              if (nameParts.length == 2) {
                String firstName = nameParts[0];
                String lastName = nameParts[1];
                Person person = new Person(firstName, lastName);
                person = comms.insertNewPerson(person);

                if (comms.linkPersonProject(person, newProject)) {
                  createdLinksCount++;
                }
              }
            }
          }
          UI.message(
              "Updated "
                  + createdLinksCount
                  + " people assigned to project '"
                  + newProject.getProjectName()
                  + "'.");
        }
      }
    } while (insertOption != 0);
  }

  static void handleUpdateOption() {
    int updateOption;
    do {
      updateOption = UI.chooseUpdateOption();

      switch (updateOption) {
        // Return to main menu
        case 0 -> UI.returnToMainMenu();

        // Update persons info
        case 1 -> {
          String firstName = UI.getInput("Enter person's CURRENT first name: ");
          String lastName = UI.getInput("Enter person's CURRENT last name: ");
          Person person = new Person(firstName, lastName);

          person = comms.fetchPersonIfExists(person);
          if (person == null) {
            UI.message(
                "Person '" + firstName + " " + lastName + "' does not exist in the database.");
            continue;
          }

          String newFirstName = UI.getInput("Enter person's NEW first name: ");
          String newLastName = UI.getInput("Enter person's NEW last name: ");
          person.setFirstName(newFirstName);
          person.setLastName(newLastName);
          if (comms.updatePerson(person)) {
            UI.message(
                "Person updated successfully to '" + newFirstName + " " + newLastName + "'.");
          } else {
            UI.message("Failed to update person.");
          }
        }

        // Update skill name
        case 2 -> {
          String skillName = UI.getInput("Enter CURRENT skill name: ");
          Skill skill = new Skill(skillName);

          skill = comms.fetchSkillIfExists(skill);
          if (skill == null) {
            UI.message("Skill '" + skillName + "' does not exist in the database.");
            continue;
          }

          String newSkillName = UI.getInput("Enter NEW skill name: ");
          skill.setSkillName(newSkillName);
          if (comms.updateSkill(skill)) {
            UI.message("Skill updated successfully to '" + newSkillName + "'.");
          } else {
            UI.message("Failed to update skill.");
          }
        }

        // Update project name
        case 3 -> {
          String projectName = UI.getInput("Enter CURRENT project name: ");
          Project project = new Project(projectName);

          project = comms.fetchProjectIfExists(project);
          if (project == null) {
            UI.message("Project '" + projectName + "' does not exist in the database.");
            continue;
          }

          String newProjectName = UI.getInput("Enter NEW project name: ");
          project.setProjectName(newProjectName);
          if (comms.updateProject(project)) {
            UI.message("Project updated successfully to '" + newProjectName + "'.");
          } else {
            UI.message("Failed to update project.");
          }
        }
      }
    } while (updateOption != 0);
  }

  static void handleDeleteOption() {
    int deleteOption;
    do {
      deleteOption = UI.chooseDeleteOption();

      switch (deleteOption) {
        // Return to main menu
        case 0 -> UI.returnToMainMenu();

        // Delete person
        case 1 -> {
          String firstName = UI.getInput("Enter person's first name: ");
          String lastName = UI.getInput("Enter person's last name: ");
          Person person = new Person(firstName, lastName);

          person = comms.fetchPersonIfExists(person);
          if (person == null) {
            UI.message(
                "Person '" + firstName + " " + lastName + "' does not exist in the database.");
            continue;
          }

          comms.deletePerson(person);
        }

        // Delete person's skills
        case 2 -> {
          String firstName = UI.getInput("Enter person's first name: ");
          String lastName = UI.getInput("Enter person's last name: ");
          Person person = new Person(firstName, lastName);

          person = comms.fetchPersonIfExists(person);
          if (person == null) {
            UI.message(
                "Person '" + firstName + " " + lastName + "' does not exist in the database.");
            continue;
          }

          // Would be nice to show person's skills here before asking which to delete

          String skills =
              UI.getInput("Enter skills to delete from this person [comma separated]: ");

          String[] skillNames = skills.split(",");
          int deletedLinksCount = 0;
          for (String s : skillNames) {
            s = s.trim();
            if (!s.isEmpty()) {
              Skill skill = new Skill(s);
              skill = comms.fetchSkillIfExists(skill);

              if (skill != null && comms.unlinkPersonSkill(person, skill)) {
                deletedLinksCount++;
              }
            }
          }
          UI.message(
              "Deleted "
                  + deletedLinksCount
                  + " skills from "
                  + person.getFirstName()
                  + " "
                  + person.getLastName()
                  + ".");
        }

        // Delete skill
        case 3 -> {
          String skillName = UI.getInput("Enter skill name: ");
          Skill skill = new Skill(skillName);

          skill = comms.fetchSkillIfExists(skill);
          if (skill == null) {
            UI.message("Skill '" + skillName + "' does not exist in the database.");
            continue;
          }

          comms.deleteSkill(skill);
        }

        // Delete project
        case 4 -> {
          String projectName = UI.getInput("Enter project name: ");
          Project project = new Project(projectName);

          project = comms.fetchProjectIfExists(project);
          if (project == null) {
            UI.message("Project '" + projectName + "' does not exist in the database.");
            continue;
          }

          comms.deleteProject(project);
        }

        // Delete skills required for a project
        case 5 -> {
          String projectName = UI.getInput("Enter project name: ");
          Project project = new Project(projectName);

          project = comms.fetchProjectIfExists(project);
          if (project == null) {
            UI.message("Project '" + projectName + "' does not exist in the database.");
            continue;
          }

          // Would be nice to show skills required by this project here before asking which to
          // delete

          String skills =
              UI.getInput(
                  "Enter skills to delete from this project requirements [comma separated]: ");

          String[] skillNames = skills.split(",");
          int deletedLinksCount = 0;
          for (String s : skillNames) {
            s = s.trim();
            if (!s.isEmpty()) {
              Skill skill = new Skill(s);
              skill = comms.fetchSkillIfExists(skill);

              if (skill != null && comms.unlinkProjectSkill(project, skill)) {
                deletedLinksCount++;
              }
            }
          }
          UI.message(
              "Deleted "
                  + deletedLinksCount
                  + " required skills from "
                  + project.getProjectName()
                  + ".");
        }

        // Delete people assigned to a project
        case 6 -> {
          String projectName = UI.getInput("Enter project name: ");
          Project project = new Project(projectName);

          project = comms.fetchProjectIfExists(project);
          if (project == null) {
            UI.message("Project '" + projectName + "' does not exist in the database.");
            continue;
          }

          // Would be nice to show people working on this project here before asking which to delete

          String people =
              UI.getInput(
                  "Enter people to unassign from this project [format: First Last,comma separated]:"
                      + " ");

          String[] peopleNames = people.split(",");
          int deletedLinksCount = 0;
          for (String p : peopleNames) {
            p = p.trim();
            if (!p.isEmpty()) {
              String[] nameParts = p.split(" ");
              Person person = new Person(nameParts[0], nameParts[1]);
              person = comms.fetchPersonIfExists(person);

              if (person != null && comms.unlinkPersonProject(person, project)) {
                deletedLinksCount++;
              }
            }
          }
          UI.message(
              "Deleted " + deletedLinksCount + " people from " + project.getProjectName() + ".");
        }
      }
    } while (deleteOption != 0);
  }
}
