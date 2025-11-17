package com.example.week2;

public class App {

    static DBcomms comms;
    
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, from week2!");
        comms = new DBcomms("TestingUser", "TestingUserPass");

        int option;
        do{
            option = UI.chooseOption();

            switch (option){
                // Close app
                case 0 -> System.out.println("Closing app. Goodbye " + comms.user + "!");

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

    static void handleSelectOption(){
        int selectOption;
        do{
            selectOption = UI.chooseSelectOption();

            System.out.println("You chose SELECT option: " + selectOption);
            switch (selectOption){
                // Return to main menu
                case 0 -> System.out.println("Returning to main menu.");

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
                case 7 -> MyUtils.notImplementedYet();
            }
        } while (selectOption != 0);
    }
} 