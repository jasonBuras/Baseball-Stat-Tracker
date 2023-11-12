package Baseball

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class UI{

    private List<Team> teams;
    private static Scanner input;
    private PersistenceManager pm;
    private PlayerDataHandler exporter;

    public UI(){
        this.pm = new PersistenceManager();
        input = new Scanner(System.in);
        this.teams = pm.loadAllTeams();
        if(teams.size() > 0){
            for(Team team : teams){
                team.setRoster(new PlayerDataHandler().importPlayers(team));
                System.out.println("Roster set for " + team.getName());
            }
        }else{
            System.out.println("No teams currently present");
        }
        this.exporter = new PlayerDataHandler();
        delay(500);
    }

    public void mainMenu() {

        while (true) {
            System.out.println("Main Menu:");
            System.out.println("1. Add/Remove Teams");
            System.out.println("2. Edit Team");
            System.out.println("3. Player Search");
            System.out.println("4. View Team Rosters");
            System.out.println("5. Live Game");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            int choice = input.nextInt();
            input.nextLine(); // Consume newline left-over

            switch (choice) {
                case 1:
                    addRemoveTeams();
                    break;
                case 2:
                    editTeam();
                    break;
                case 3:
                    playerSearch();
                    break;
                case 4:
                    viewRosters();
                    break;
                case 5:
                    startGame();
                    break;
                case 0:
                    System.out.println("Exiting the program.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public void startGame(){
        boolean isHomeTeam = false;
        System.out.println("Which team is playing?");
        for (int i = 0; i < teams.size(); i++) {
            System.out.println(i + " - " + teams.get(i).getName());
        }
        System.out.println("Enter the number of the team playing, or " + teams.size() + " to cancel:");
        int teamIndex = input.nextInt();
        input.nextLine(); // Consume newline left-over

        // Check if the user wants to cancel the operation
        if (teamIndex == teams.size()) {
            return;
        }

        // Validate the input
        if (teamIndex < 0 || teamIndex >= teams.size()) {
            System.out.println("Invalid team number. Please try again.");
            return;
        }
        Team currentTeam = teams.get(teamIndex);

        System.out.print("Name of opposing team: ");
        String opposingTeam = input.nextLine();

        boolean validResponse = false;
        do{
            System.out.println("Is " + currentTeam.getName() + " batting first?\nType \"Yes\" or \"No\"");
            String response = input.nextLine().toLowerCase();
            switch (response){
                case "yes": validResponse=true; break;
                case "no" : isHomeTeam = true; validResponse=true; break;
                default: System.out.println("Invalid response");break;
            }

        }while (!validResponse);

        Game game = new Game(opposingTeam, currentTeam, isHomeTeam);
        game.liveGame();

    }

    private void viewRosters() {
        System.out.println("Select a team to view:");
        for (int i = 0; i < teams.size(); i++) {
            System.out.println(i + " - " + teams.get(i).getName() + " (" + teams.get(i).getPlayerCount() + " Players)");
        }
        System.out.println("Enter the number of the team you wish to edit, or " + teams.size() + " to cancel:");
        int teamIndex = input.nextInt();
        input.nextLine(); // Consume newline left-over

        // Check if the user wants to cancel the operation
        if (teamIndex == teams.size()) {
            return;
        }

        // Validate the input
        if (teamIndex < 0 || teamIndex >= teams.size()) {
            System.out.println("Invalid team number. Please try again.");
            return;
        }

        boolean exit = false;
        int selection = 0;
        Team selectedTeam = teams.get(teamIndex);
        while(!exit){
            if(selection==0){//hitting stats
                selectedTeam.getRoster().statsTable(0);
                System.out.println("To view pitching stats, type 1\nTo exit, type 2");
                selection = input.nextInt();
                input.nextLine();
            }

            if(selection==1){//pitching stats
                pitchingStatsHeader();
                selectedTeam.getRoster().statsTable(1);
                System.out.println("To view hitting stats, type 0\nTo exit, type 2");
                selection = input.nextInt();
                input.nextLine();
            }


            if(selection==2){
                exit=true;
            }else{
                System.out.println("[Invalid input]");
            }
        }

    }

    private void addRemoveTeams() {
        while(true){
            int teamCount = 0;
            System.out.println("Teams:");
            for (Team team : teams) {
                System.out.println(teamCount + " - " + team.getName());
                teamCount++;
            }
            System.out.println(teamCount + " - Add a new team");
            System.out.println((teamCount + 1) + " - Go back to main menu");

            System.out.print("\nEnter the number to REMOVE a team, ADD a new team, or go back: ");
            int choice = input.nextInt();
            input.nextLine(); // Consume newline left-over

            if(choice == -1){
                Team greenwave = new Team("NBC Greenwave", 10);
                teams.add(greenwave);
                System.out.println("Greeeeeenwave");
                saveTeamData(greenwave);
                return;
            }

            if (choice == teamCount) {
                // Add a new team
                addTeam();
                break; // Break if you want to return to the main menu after adding a team
            } else if (choice == teamCount + 1) {
                // Go back to main menu
                return;
            } else if (choice >= 0 && choice < teamCount) {
                // Remove the selected team
                teams.remove(choice);
                System.out.println("Team removed successfully.");
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void addTeam() {

        System.out.print("Enter the name of the new team: ");
        String teamName = input.nextLine().trim();


        System.out.print("Enter the age group of the new team: ");
        int teamAge = input.nextInt();
        input.nextLine();

        Team newTeam = new Team(teamName, teamAge);
        teams.add(newTeam);
        System.out.println("Team added successfully.");
        // Here you can add the option to add players to the new team

        saveTeamData(newTeam);

    }

    private void editTeam() {
        System.out.println("Select a team to edit:");
        for (int i = 0; i < teams.size(); i++) {
            System.out.println(i + " - " + teams.get(i).getName() + " (" + teams.get(i).getPlayerCount() + " Players)");
        }
        System.out.println("Enter the number of the team you wish to edit, or " + teams.size() + " to cancel:");

        int teamIndex = input.nextInt();
        input.nextLine(); // Consume newline left-over

        // Check if the user wants to cancel the operation
        if (teamIndex == teams.size()) {
            return;
        }

        // Validate the input
        if (teamIndex < 0 || teamIndex >= teams.size()) {
            System.out.println("Invalid team number. Please try again.");
            return;
        }

        Team selectedTeam = teams.get(teamIndex);

        while (true) {
            System.out.println("Editing Team: " + selectedTeam.getName());
            System.out.println("1 - Change team name");
            System.out.println("2 - Add player(s) to roster");
            System.out.println("3 - Remove player from roster");
            System.out.println("4 - Edit player on roster");
            System.out.println("5 - Reset Team Stats");
            System.out.println("0 - Go back to main menu");

            System.out.print("Enter your choice: ");
            int choice = input.nextInt();
            input.nextLine(); // Consume newline left-over

            switch (choice) {
                case 1:
                    changeTeamName(selectedTeam);
                    break;
                case 2:
                    addPlayerToRoster(selectedTeam);
                    try{
                        exporter.exportPlayers(selectedTeam, selectedTeam.getRoster());
                    }catch (IOException e){
                        System.err.println("Failed to export team players: " + e.getMessage());
                    }
                    break;
                case 3:
                    removePlayerFromRoster(selectedTeam);
                    break;
                case 4:
                    editPlayerOnRoster(selectedTeam);
                    break;
                case 5:
                    int sure = 0;
                    do {
                        System.out.println("Note: Doing this will reset every player's stats to 0\nARE YOU SURE YOU WANT TO DO THIS?");
                        delay(2500);
                        System.out.println("1-YES\n2-NO");
                        sure = input.nextInt();
                        input.nextLine();
                        if (sure==1){
                            selectedTeam.resetStats();
                            System.out.println("Reset stats for all players on " + selectedTeam.getName());
                            delay(1000);
                        }else{
                            System.out.println("Returning");
                            delay(1000);
                            return;
                        }
                    }while(sure < 1 || sure > 2);
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void changeTeamName(Team team) {
        System.out.print("Enter the new name for the team: ");
        String newName = input.nextLine();
        team.setName(newName);
        System.out.println("Team name changed to: " + newName);
        delay(1500);
    }

    private void addPlayerToRoster(Team team) {
        System.out.println("Select an option:");
        System.out.println("1 - Import players from a .csv file");
        System.out.println("2 - Add player(s) manually");
        System.out.print("Note: If you do not have a .csv file present in the team directory, a blank .csv file with the proper template will be created.\n" +
                         "Enter your choice: ");
        int choice = input.nextInt();
        input.nextLine(); // Consume newline left-over

        switch (choice) {
            case 1:
                PlayerList importedPlayers = new PlayerDataHandler().importPlayers(team);
                for (Player player : importedPlayers.getPlayers()) {
                    System.out.println(player.getName() + " added to roster for " + team.getName() + team.getAgeGroup() +"U.");
                    team.addPlayer(player);
                }
                System.out.println(importedPlayers + " players imported successfully.");

                break;
            case 2:
                System.out.print("How many players would you like to add? ");
                int numberOfPlayersToAdd = input.nextInt();
                input.nextLine(); // Consume newline left-over

                for (int i = 0; i < numberOfPlayersToAdd; i++) {
                    System.out.println("Adding player " + (i + 1) + " of " + numberOfPlayersToAdd);
                    System.out.print("Enter player's first name: ");
                    String firstName = input.nextLine();
                    System.out.print("Enter player's last name: ");
                    String lastName = input.nextLine();
                    System.out.print("Enter player's jersey number: ");
                    int jerseyNumber = input.nextInt();
                    input.nextLine(); // Consume newline left-over

                    Player newPlayer = new Player(firstName, lastName, jerseyNumber);
                    team.getRoster().addPlayer(newPlayer);
                    System.out.println(newPlayer.getName() + " added to roster for " + team.getName() + team.getAgeGroup() +"U.");
                }
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
        saveTeamData(team);
    }

    private void removePlayerFromRoster(Team team) {
        System.out.print("Enter player's first name: ");
        String firstName = input.nextLine();
        System.out.print("Enter player's last name: ");
        String lastName = input.nextLine();
        System.out.print("Enter player's jersey number: ");
        int jerseyNumber = input.nextInt();
        input.nextLine(); // Consume newline left-over

        boolean removed = team.removePlayer(firstName, lastName, jerseyNumber);
        if (removed) {
            System.out.println("Player removed from roster.");
        } else {
            System.out.println("Player not found or could not be removed.");
        }

        saveTeamData(team);

    }

    private void editPlayerOnRoster(Team team) {
        System.out.print("Enter player's first name: ");
        String firstName = input.nextLine();
        System.out.print("Enter player's last name: ");
        String lastName = input.nextLine();
        System.out.print("Enter player's jersey number: ");
        int jerseyNumber = input.nextInt();
        input.nextLine(); // Consume newline left-over

        Player playerToEdit = team.getRoster().findPlayer(firstName, lastName, jerseyNumber);

        if (playerToEdit == null) {
            System.out.println("Player not found.");
            return;
        }

        boolean done = false;
        while (!done) {
            System.out.println("Editing Player: #" + playerToEdit.getJerseyNumber() + " " + playerToEdit.getFirstName() + " " + playerToEdit.getLastName());
            System.out.println("1 - Edit first name");
            System.out.println("2 - Edit last name");
            System.out.println("3 - Edit jersey number");
            System.out.println("4 - Edit stats");
            System.out.println("0 - Done editing");

            System.out.print("Enter your choice: ");
            int choice = input.nextInt();
            input.nextLine(); // Consume newline left-over

            switch (choice) {
                case 1:
                    System.out.print("Enter the new first name: ");
                    playerToEdit.setFirstName(input.nextLine());
                    break;
                case 2:
                    System.out.print("Enter the new last name: ");
                    playerToEdit.setLastName(input.nextLine());
                    break;
                case 3:
                    System.out.print("Enter the new jersey number: ");
                    playerToEdit.setJerseyNumber(input.nextInt());
                    input.nextLine(); // Consume newline left-over
                    break;
                case 4:
                    editPlayerStats(playerToEdit);
                    break;
                case 0:
                    done = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    delay(1000);
            }

            saveTeamData(playerToEdit.getTeam());

        }
    }

    private void editPlayerStats(Player playerToEdit) {
        boolean done = false;
        while (!done) {
            System.out.println("Select the stat to edit for " + playerToEdit.getFirstName() + " " + playerToEdit.getLastName() + ":");
            System.out.println("1 - At Bats");
            System.out.println("2 - Hits");
            System.out.println("3 - Walks");
            System.out.println("4 - Home Runs");
            System.out.println("5 - Pitches");
            System.out.println("6 - Strikes");
            System.out.println("7 - Balls");
            System.out.println("8 - Strikeouts");
            System.out.println("9 - Walks Pitched");
            System.out.println("0 - Done editing stats");

            System.out.print("Enter your choice: ");
            int choice = input.nextInt();
            input.nextLine(); // Consume newline left-over

            switch (choice) {
                case 1:
                    System.out.print("Enter the new number of At Bats: ");
                    playerToEdit.getStats().setAtBats(input.nextInt());
                    break;
                case 2:
                    System.out.print("Enter the new number of Hits: ");
                    playerToEdit.getStats().setHits(input.nextInt());
                    break;
                case 3:
                    System.out.print("Enter the new number of Walks: ");
                    playerToEdit.getStats().setBoB(input.nextInt());
                    break;
                case 4:
                    System.out.print("Enter the new number of Home Runs: ");
                    playerToEdit.getStats().setHomeRuns(input.nextInt());
                    break;
                case 5:
                    System.out.print("Enter the new number of Pitches: ");
                    playerToEdit.getStats().setPitchCount(input.nextInt());
                    break;
                case 6:
                    System.out.print("Enter the new number of Strikes: ");
                    playerToEdit.getStats().setStrikes(input.nextInt());
                    break;
                case 7:
                    System.out.print("Enter the new number of Balls: ");
                    playerToEdit.getStats().setBalls(input.nextInt());
                    break;
                case 8:
                    System.out.print("Enter the new number of Strikeouts: ");
                    playerToEdit.getStats().setStrikeouts(input.nextInt());
                    break;
                case 9:
                    System.out.print("Enter the new number of Walks Pitched: ");
                    playerToEdit.getStats().setWalksPitched(input.nextInt());
                    break;
                case 0:
                    done = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    delay(1000);
            }
            saveTeamData(playerToEdit.getTeam());
            input.nextLine(); // Consume newline left-over after int input
        }
    }

    public void saveTeamData(Team team){
        pm.saveTeam(team);
    }

    public Team loadTeamData(String teamName){
        return pm.loadTeam(teamName);
    }

    private void playerSearch() {

        System.out.println("Please enter a name in the order:\nLastName FirstName Jersey#\n\nImportant Note: " +
                "If a name has a space, enclose it in quotes (e.g., \"St. Anne\" John 12).\n" +
                "You can leave the first name and/or jersey number blank.");

        String inputLine = input.nextLine();
        String[] parts = inputLine.split("\\s+");

        String lastName = "";
        String firstName = "";
        Integer jerseyNumber = null;

// Parse the input based on the number of parts
        switch (parts.length) {
            case 1:
                // Only one part, treat it as the last name
                lastName = parts[0];
                break;
            case 2:
                // Could be "LastName JerseyNumber" or "LastName FirstName"
                try {
                    jerseyNumber = Integer.parseInt(parts[1]);
                    lastName = parts[0];
                } catch (NumberFormatException e) {
                    lastName = parts[0];
                    firstName = parts[1];
                }
                break;
            case 3:
                // Assume "LastName FirstName JerseyNumber"
                lastName = parts[0];
                firstName = parts[1];
                try {
                    jerseyNumber = Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid jersey number. Please try again.");
                    return;
                }
                break;
            default:
                // More than three parts, assume the first two are the last name and the rest is first name and jersey number
                lastName = parts[0] + " " + parts[1]; // Concatenate the first two parts for the last name
                firstName = parts[2];
                if (parts.length > 3) {
                    try {
                        jerseyNumber = Integer.parseInt(parts[3]);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid jersey number. Please try again.");
                        return;
                    }
                }
                break;
        }

        PlayerList searchResults = new PlayerList();
        for(Team team : teams) {
            if (jerseyNumber != null) {
                if (firstName != null && !firstName.isEmpty()) {//to search all 3
                    searchResults = team.getRoster().search(lastName, firstName, jerseyNumber);
                } else {//last name & jersey #
                    searchResults = team.getRoster().search(lastName, jerseyNumber);
                }
            } else if (firstName != null && !firstName.isEmpty()) {//last name & first name
                searchResults = team.getRoster().search(lastName,firstName);
            }else{//last name only
                searchResults = team.getRoster().search(lastName);
            }
        }

        // Display the results
        if (searchResults.size() == 0){
            System.out.println("No player " + lastName + ", " + firstName + " #" + jerseyNumber + " exists");
        }
        if(searchResults.size() == 1){
            Player selectedPlayer = searchResults.get(0);
            boolean finished = false;
            int selection = 0;
            while(!finished){
                if(selection==0){//intentional
                    selectedPlayer.printHittingStats(true);
                    System.out.println("Type 1 to view pitching stats or 2 to exit");
                    selection = input.nextInt();
                    input.nextLine();

                }
                if(selection==1){
                    selectedPlayer.printPitchingStats(true);
                    System.out.println("Type 0 to view hitting stats or 2 to exit");
                    selection = input.nextInt();
                    input.nextLine();
                }

                if(selection == 2){
                    finished=true;
                }else{
                    System.out.println("Invalid input");
                    delay(500);
                }
            }
        }

        int currentPage = 1;
        int pageSize = 15;//results per page
        boolean exit =false;
        while(!exit){
            int startIndex = (currentPage - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, searchResults.size());

            //current page of results
            for (int i = startIndex; i < endIndex; i++) {
                Player player = searchResults.get(i);
                System.out.println((i % pageSize + 1) + ". " + player.getFirstName() + " " + player.getLastName() + " #" + player.getJerseyNumber());
            }

            // Pagination and options
            System.out.println("\nPage " + currentPage + " of " + ((searchResults.size() - 1) / pageSize + 1));
            System.out.println("[N] Next page | [P] Previous page | [S] Select player | [E] Exit");
            String option = input.nextLine().toUpperCase();

            switch (option) {
                case "N":
                    if (currentPage * pageSize < searchResults.size()) {
                        currentPage++;
                    } else {
                        System.out.println("You are on the last page.");
                    }
                    break;
                case "P":
                    if (currentPage > 1) {
                        currentPage--;
                    } else {
                        System.out.println("You are on the first page.");
                    }
                    break;
                case "S":
                    System.out.println("Enter the index of the player to view stats:");
                    int index = Integer.parseInt(input.nextLine()) - 1 + startIndex;
                    if (index >= startIndex && index < endIndex) {

                        Player selectedPlayer = searchResults.get(index);
                        boolean exit2 = false;
                        int selection = 0;
                        while(!exit2){
                            if(selection==0){//intentional
                                selectedPlayer.printHittingStats(true);
                                System.out.println("Type 1 to view pitching stats or 2 to exit");
                                selection = input.nextInt();
                                input.nextLine();

                            }
                            if(selection==1){
                                selectedPlayer.printPitchingStats(true);
                                System.out.println("Type 0 to view hitting stats or 2 to exit");
                                selection = input.nextInt();
                                input.nextLine();
                            }

                            if(selection == 2){
                                exit2=true;
                            }else{
                                System.out.println("Invalid input");
                                delay(500);
                            }
                        }

                    } else {
                        System.out.println("Invalid index.");
                    }
                    break;
                case "E":
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }

    private void pitchingStatsHeader(){
        System.out.printf("%-4s %-20s %-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s%n",
                "Jersey", "Name", "IP", "PC", "ST", "B", "H", "BB", "SO", "WHIP");
    }
    private void hittingStatsHeader(){
        System.out.printf("%-4s %-20s %-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s%n",
                "Jersey", "Name", "AVG", "AB", "1B", "2B", "3B", "HR", "RBI", "BB", "OBP", "SLG");
    }

    // Additional helper methods for each functionality

    private void delay(long milliseconds){
        try {
            Thread.sleep(milliseconds); // 1000 milliseconds = 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Handle the interruption
        }
    }






}