package Baseball

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Game implements Serializable {
    private String gameLogDirectory;
    private boolean isHomeTeam;
    private PlayerList gamePlayerList;//import roster, reset stats to 0, save Game/stats
    private Player currentPitcher;
    private int currentBatterIndex;
    private int teamAscore;//us
    private int teamBscore;//them
    private String opposingTeam;
    private List<Team> teams;
    private Team team;
    private final Scanner input;
    private LocalDateTime now;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
    private String formattedDateTime;
    private String filename;
    private boolean gameOver;
    private List<String> gameLog;
    private PersistenceManager pm;



    public Game(String opposingTeam, Team teamA, boolean isHomeTeam){
        this.currentPitcher = null;
        this.opposingTeam = opposingTeam;
        this.team = teamA;
        this.isHomeTeam = isHomeTeam;
        this.currentBatterIndex = 0;
        this.teamAscore = 0;
        this.teamBscore = 0;
        this.input = new Scanner(System.in);
        this.gamePlayerList = new PlayerList();
        this.now = LocalDateTime.now();
        this.formattedDateTime = now.format(formatter).toLowerCase();
        this.filename = team.getFilePath() + formattedDateTime + ".csv";
        this.opponentBatter = 1;
        this.gameOver=false;
        this.gameLog = new ArrayList<>();
        this.gameStats = new HashMap<>();
        this.gameLogDirectory = team.gameLogPath() + formattedDateTime + ".csv";
        this.pm = new PersistenceManager();
    }

    private Map<Player, Map<String, Integer>> gameStats;
    public void liveGame() {

        /*
        TODO: Add current count to Live Scoreboard
        When player is out, reset strikes and balls to 0 (done)
         Add delay to message between innings (done)
         Add print statements that "narrate" the game as it's happening (done, and added log)
         Lineup order broken. Went 1, 2, 3 in the first inning and 1, 2, 3 again in the second inning.
         */


        Team currentTeam = team;

        System.out.println("How many innings?");
        int innings = input.nextInt();
        input.nextLine();

        if(currentTeam.getLineup().size() ==0){
            currentTeam.setLineup(setBattingOrder(currentTeam));
        }else{
            do{
                currentTeam.getRoster().statsTable(0);
                System.out.println("Is this still your current lineup?\n1 - Yes\n2 - No");
                int selection = input.nextInt();
                input.nextLine();

                if(selection==1){
                    do{
                        System.out.print("Enter the jersey number for your starting pitcher today: ");
                        int jerseyNumber = input.nextInt();
                        input.nextLine();
                        try{
                            this.currentPitcher = currentTeam.getRoster().getPlayerByJerseyNumber(jerseyNumber);
                            break;
                        }catch (NoSuchElementException e) {
                            System.out.println("No player with jersey number " + jerseyNumber + " found. Please try again: ");
                            delay(2000);
                        }
                    }while (true);
                }
                if(selection==2){
                    currentTeam.setLineup(setBattingOrder(currentTeam));
                    pm.saveTeam(currentTeam);
                }else{
                    System.out.println("Invalid input");
                    delay(1000);
                }

            }while(true);
        }

        mapPlayers(team.getLineup());


        currentBatterIndex=0;
        int currentInning = 1;
        int topOrBottom = 0; //0-top of inning, 1-bottom of inning
        boolean currentTeamUpToBat = false;
        gameLog.add("Today we have a match-up between your " + team.getName() + " and " + opposingTeam);
        gameLog.add("The starting line up for " + team.getName() + " is: ");
        int battingPosition = 1;
        for(Player player : team.getLineup().getPlayers()){
            if(player != null){
                gameLog.add(battingPosition + ". " + player.getName());
                battingPosition++;
            }
        }
        gameLog.add("And your starting pitcher is: " + currentPitcher.getName());
        try {
            exportGameLog();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        do{
            if (topOrBottom == 0) {//top of inning
                if(!isHomeTeam){
                    displayBattingUI(currentBatterIndex, currentInning, topOrBottom);
                    try {
                        exportGameLog();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    currentTeamUpToBat = true;
                }else{
                    displayPitchingUI(currentInning, currentTeam, topOrBottom);
                    try {
                        exportGameLog();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    currentTeamUpToBat = false;
                }

            }
            if(topOrBottom == 1){//bottom of inning
                if(isHomeTeam){
                    displayBattingUI(currentBatterIndex, currentInning, topOrBottom);
                }else{
                    displayPitchingUI(currentInning, currentTeam, topOrBottom);
                }

            }

            try {
                PlayerDataHandler exporter = new PlayerDataHandler();
                exporter.exportPlayers(currentTeam, currentTeam.getRoster());
                System.out.println("Player data saved!");
                delay(500);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if(topOrBottom==0){
                topOrBottom = 1;
                currentTeamUpToBat = !currentTeamUpToBat;//toggles boolean
            }else{
                topOrBottom = 0;
                currentInning++;
                currentTeamUpToBat = !currentTeamUpToBat;//toggles boolean
            }



            if (currentInning > innings && teamAscore != teamBscore) {
                gameOver=true;
                System.out.println("Game Over");
                System.out.println(teamAscore > teamBscore ? currentTeam.getName() + " wins." : opposingTeam + " wins. :(");
                gameLog.add("And your final for today is " +team.getName() + ": " + teamAscore + " - " + opposingTeam + ": " + teamBscore);
            } else if (currentInning == innings && teamAscore == teamBscore && topOrBottom==0) {//if last inning ends in tie score, add extra inning
                System.out.println("Extra Baseball!");
                gameLog.add(innings + " innings wasn't enough, we're headed to inning number " + (innings + 1));
                innings++;
                System.out.println("Moving on to inning " + innings);

            }else if (currentInning != innings && topOrBottom != 1){
                System.out.println(halfInningUpdate(currentBatterIndex, topOrBottom, currentInning, currentTeam, currentTeamUpToBat));
                delay(2000);
            }

            if(gameOver){
                try {
                    exportGameLog();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                do{
                    System.out.println("Would you like to view:\n" +
                            "1 - Game Log\n" +
                            "2 - Player Stats (for this game)\n" +
                            "0 - End Game");
                    int selection = input.nextInt();
                    input.nextLine();

                    switch (selection){
                        case 0: break;
                        case 1:
                            for (String log : gameLog){
                                System.out.println(log); break;
                            }
                        case 2:
                            displayGameStats(gameStats); break;
                    }

                }while (true);
            }

        }while(!gameOver);



    }

    private void mapPlayers(PlayerList lineup) {
        for (Player player : lineup.getPlayers()) {
            Map<String, Integer> stats = new HashMap<>();
            stats.put("Hits", 0);
            stats.put("AtBats", 0);
            stats.put("HomeRuns", 0);
            stats.put("RBIs", 0);
            stats.put("HBP", 0);
            // For pitchers
            stats.put("InningsPitched", 0);
            stats.put("HitsAllowed", 0);
            stats.put("Walks", 0);
            stats.put("StrikeOuts", 0);

            gameStats.put(player, stats);
        }
    }

    private String halfInningUpdate(int currentBatterIndex, int topOrBottom, int currentInning, Team currentTeam, boolean displayNextBatters){
        PlayerList lineup = currentTeam.getLineup();
        String inningPart = topOrBottom == 0 ? "top" : "bottom";
        String scoreUpdate = "Heading into the " + inningPart + " of inning " + currentInning +
                ", the score is\n" + currentTeam.getName()+ " " + teamAscore + " - " + opposingTeam + " " + teamBscore + ".";
        gameLog.add(scoreUpdate);
        String nextBatters = "";
        if(displayNextBatters){
            nextBatters = "Due up the next inning: " +
                    lineup.getPlayerByIndex(currentBatterIndex).getName() + ", " +
                    lineup.getPlayerByIndex((currentBatterIndex + 1) % lineup.size()).getName() + ", " +
                    lineup.getPlayerByIndex((currentBatterIndex + 2) % lineup.size()).getName() + ".";
            gameLog.add(nextBatters);
        }
        try {
            exportGameLog();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return scoreUpdate + "\n" + nextBatters;

    }

    private void displayBattingUI(int currentBatterIndex, int currentInning, int topOrbottom){//0-top of inning, 1-bottom of inning
        int outs = 0;
        while(outs<3) {
            Player currentBatter = team.getLineup().getPlayerByIndex(currentBatterIndex);
            Map<String, Integer> playerStats = gameStats.get(currentBatter);
            scoreboard(currentInning, topOrbottom, outs, team.getName());
            System.out.println("Now batting: " + currentBatter.getName());
            System.out.println("1 - Hit");
            System.out.println("2 - Home Run");//remove?
            System.out.println("3 - Out");
            System.out.println("4 - Hit By Pitch");
            System.out.println("5 - Reached on Error");
            System.out.println("6 - Pinch Hitter");
            System.out.println("7 - Extra Innings*");
            System.out.println("8 - Change Score*");
            System.out.println("9 - View Current Game Stats");
            System.out.println("0 - End Inning");
            System.out.println("(*) Hasn't been implemented yet.");
            int action = input.nextInt();
            input.nextLine(); // Consume newline left-over

            switch (action) {
                case 1:
                    int bases = 0;
                    do {
                        System.out.println("1 - Single\n" +
                                "2 - Double\n" +
                                "3 - Triple\n" +
                                "4 - HR*\n");
                        bases = input.nextInt();
                        input.nextLine();
                    } while (bases < 1 || bases > 4);
                    int RBIs = 0;
                    do {
                        System.out.println("How many runs scored? (0-4)");
                        RBIs = input.nextInt();
                        input.nextLine();
                    } while (RBIs < 0 || RBIs > 4);
                    teamAscore += RBIs;
                    if(RBIs==0){
                        gameLog.add(currentBatter.getName() + " got a hit");
                    }else{
                        gameLog.add(currentBatter.getName() + " got a hit, scoring " + RBIs + " runs.");
                    }
                    if(RBIs == 4){
                        System.out.println(getRandomGrandslamMessage());
                        delay(1000);
                    }

                    currentBatter.addHit(bases, RBIs);
                    playerStats.put("Hits", playerStats.get("Hits") + 1);
                    playerStats.put("RBIs", playerStats.get("RBIs") + RBIs);
                    break;
                case 2:
                    do {
                        System.out.println("How many runs scored? (1-4)");
                        RBIs = input.nextInt();
                        input.nextLine();
                    } while (RBIs < 1 || RBIs > 4);
                    teamAscore += RBIs;
                    currentBatter.addHit(4,RBIs);
                    playerStats.put("Hits", playerStats.get("Hits") + 1);
                    playerStats.put("HomeRuns", playerStats.get("HomeRuns") + 1);
                    playerStats.put("RBIs", playerStats.get("RBIs") + RBIs);
                    if (RBIs == 1){
                        gameLog.add(currentBatter.getName() + " hit a solo home-run, their " + currentBatter.getStats().getHomeRuns() + " homer of the year");
                        System.out.println(currentBatter.getName() + " hit a solo home-run, their " + currentBatter.getStats().getHomeRuns() + " homer of the year");
                    }else{
                        gameLog.add(currentBatter.getName() + " hit a " + RBIs + " run homer, their " + currentBatter.getStats().getHomeRuns() + " homer of the year");
                        System.out.println(currentBatter.getName() + " hit a " + RBIs + " run homer, their " + currentBatter.getStats().getHomeRuns() + " homer of the year");
                    }



                    if(RBIs == 4){
                        System.out.println(getRandomGrandslamMessage());
                        delay(1000);
                    }
                    break;
                case 3:
                    playerStats.put("AtBats", playerStats.get("AtBats") + 1);
                    currentBatter.getStats().addAtBat(1);
                    outs++;
                    gameLog.add("Out #" + outs);
                    break;
                case 4:
                    playerStats.put("HBP", playerStats.get("HBP") + 1);
                    currentBatter.getStats().addHBP(1);
                    System.out.println(currentBatter.getName() + " got hit by the pitch");
                    gameLog.add(currentBatter.getName() + " got hit by the pitch");
                case 5:
                    System.out.println(currentBatter.getName() + " reached on error");
                    gameLog.add(currentBatter.getName() + " reached on error");
                    break;
                case 6:
                    Player playerToSubIn = null;
                    do {
                        team.getRoster().statsTable(0);
                        System.out.print("Select jersey # for player being subbed in (-1 to cancel): ");
                        int jerseyNumber = input.nextInt();
                        input.nextLine(); // Consume the newline character

                        if(jerseyNumber==-1){
                            break;
                        }

                        try {
                            // Find matching jersey # for a player on the roster
                            playerToSubIn = team.getRoster().getPlayerByJerseyNumber(jerseyNumber);
                            break;
                        } catch (NoSuchElementException e) {
                            System.out.println("No player with jersey number " + jerseyNumber + " found. Please try again.");
                        }
                    } while (true);

                    // Perform the pinch-hitter substitution
                    if (playerToSubIn != null) {
                        System.out.println(currentBatter.getName() + " is being subbed out for " + playerToSubIn.getName());
                        gameLog.add(currentBatter.getName() + " is being subbed out for " + playerToSubIn.getName());
                        pinchHitter(gameStats, team.getLineup(), currentBatterIndex, playerToSubIn);
                    }
                    break;
                case 9:
                    displayGameStats(gameStats);
                    System.out.println("Press [Enter] to continue");
                    input.nextLine();
                case 0:
                    return; // End the inning
                default:
                    System.out.println("Invalid action. Please try again.");
            }

            // Move to the next batter
            currentBatterIndex = (currentBatterIndex + 1) % 9;
        }
    }

    public void displayGameStats(Map<Player, Map<String, Integer>> gameStats) {
        for (Map.Entry<Player, Map<String, Integer>> entry : gameStats.entrySet()) {
            Player player = entry.getKey();
            Map<String, Integer> stats = entry.getValue();
            System.out.println("Stats for " + player.getName() + ": " + stats);
        }
    }

    private void scoreboard(int currentInning, int topOrbottom, int outs, String teamname) {
        String arrow = "";
        if(topOrbottom==0){
            arrow = "^";
        }else{
            arrow = "v";
        }
        System.out.println("Inning: " + currentInning + arrow + " Outs: " + outs);
        System.out.println("Score: " + teamname + ": " + teamAscore + " | " + opposingTeam + ": " + teamBscore);
    }

    private String getRandomGrandslamMessage() {
        Random rand = new Random();
        int min = 0;
        int max = 9;
        int randomNum = rand.nextInt((max-min) + 1) + min;
        switch (randomNum){
            case 0: return "Out of the park! A grand slam that will be remembered!";
            case 1: return "Grand slam! All bases cleared in one spectacular swing!";
            case 2: return "It's a grand slam homer! Talk about a game-changer!";
            case 3: return "Grand slam greatness! A moment of pure baseball bliss!";
            case 4: return "Boom! That grand slam just sent the crowd into a frenzy!";
            case 5: return "One swing, four runs! That's a grand slam for the books!";
            case 6: return "Grand slam alert! The stands erupt as the ball flies out!";
            case 7: return "A grand slam masterpiece! What an incredible hit!";
            case 8: return "The bases were loaded, and so was that hit – grand slam!";
            case 9: return "Grand slam! The ultimate slam dunk of baseball!";
        }
        return "GRANDSLAM!";
    }

    private int opponentBatter;
    public void displayPitchingUI(int currentInning, Team currentTeam, int topOrBottom) {//0-top of inning, 1-bottom of inning
        int[] count = {0,0};//{balls,strikes}
        int outs = 0;
        boolean inningOver = false;
        while (!inningOver) {
            scoreboard(currentInning, topOrBottom, outs, currentTeam.getName());
            System.out.println("Count: " + count[0] + "-" + count[1]);
            System.out.println("Pitching: " + currentPitcher.getName());
            System.out.println("Currently facing batter #" + opponentBatter + " in the lineup.");

            System.out.println("1 - Strike");
            System.out.println("2 - Ball");
            System.out.println("3 - Foul Ball");
            System.out.println("4 - In Play/Foul Ball (Out)");
            System.out.println("5 - In Play (Hit)");
            System.out.println("6 - Hit By Pitch");
            System.out.println("7 - Reached on Error");
            System.out.println("8 - Add/Remove Runs (to Opposing Team)");
            System.out.println("9 - Sub Pitcher");
            System.out.println("10 - Add/Remove out");
            System.out.println("0 - End Inning");

            System.out.print("Choose the pitch outcome: ");
            int choice = input.nextInt();
            input.nextLine(); // Consume newline left-over

            switch (choice) {
                case 1: // Strike
                    currentPitcher.addPitch(true);
                    count[1]++;
                    System.out.println("Strike " + count[1]);
                    if(count[1]==3){
                        System.out.println(getRandomStrikeOutMessage());
                        gameLog.add(currentPitcher + " strikes out batter number " + gameStats.get(currentPitcher).get("StrikeOuts"));
                        opponentBatter = (opponentBatter % 9) + 1;
                        currentPitcher.getStats().addStrikeout();
                        outs++;
                        count[0] = 0;
                        count[1] = 0;
                    }
                    break;
                case 2: // Ball
                    currentPitcher.addPitch(false);
                    count[0]++;
                    System.out.println("Ball " + count[0]);
                    if(count[0] == 4){
                        System.out.println("Take your base");
                        opponentBatter = (opponentBatter % 9) + 1;
                        currentPitcher.getStats().addWalk();
                        count[0] = 0;
                        count[1] = 0;
                    }
                    break;
                case 3: //foul ball
                    currentPitcher.getStats().addPitch(true);
                    if(count[1] != 2){
                        count[1]++;
                        System.out.println("Foul Ball");
                    }else {
                        System.out.println("Foul Ball, count remains " + count[0] + "-" + count[1]);
                    }
                    break;
                case 4: // In Play (Out)
                    currentPitcher.addHitGivenUp(false);
                    opponentBatter = (opponentBatter % 9) + 1;
                    outs++;
                    count[0] = 0;
                    count[1] = 0;
                    break;
                case 5: // In Play (Hit)
                    currentPitcher.addHitGivenUp(true);
                    opponentBatter = (opponentBatter % 9) + 1;
                    count[0] = 0;
                    count[1] = 0;
                    break;
                case 6: // Hit By Pitch
                    currentPitcher.addHitBatter(1);
                    opponentBatter = (opponentBatter % 9) + 1;
                    count[0] = 0;
                    count[1] = 0;
                    break;
                case 7: // Reached on Error
                    currentPitcher.error();
                    opponentBatter = (opponentBatter % 9) + 1;
                    count[0] = 0;
                    count[1] = 0;
                    break;
                case 8: // Add Run (to teamB)
                    System.out.println("How many runs? Use (-) to subtract points");
                    teamBscore += input.nextInt();
                    input.nextLine(); // Consume the newline character
                    // Update score, but not pitcher stats
                    break;
                case 9:
                    boolean exit = false;
                    do{
                        currentTeam.getRoster().statsTable(1);
                        System.out.print("Enter the jersey number for new pitcher (-1 to cancel): ");
                        int jerseyNumber = input.nextInt();
                        input.nextLine();
                        if(jerseyNumber==-1){
                            break;
                        }
                        try {
                            currentPitcher = currentTeam.getRoster().getPlayerByJerseyNumber(jerseyNumber);
                            exit = true;
                        } catch (NoSuchElementException e) {
                            System.out.println("No player with jersey number " + jerseyNumber + " found. Please try again: ");
                            delay(2000);
                            jerseyNumber = input.nextInt();
                            input.nextLine();
                        }
                    }while (!exit);
                case 10:
                    int selection = 0;
                    do{
                        System.out.println("1 - Add\n2 - Remove\n3 - Cancel");
                        selection=input.nextInt();
                        input.nextLine();
                        if(selection==1){
                            outs++;
                        } else if (selection==2) {
                            outs--;
                        } else if (selection==3) {
                            break;
                        }else{
                            System.out.println("Invalid input");
                            delay(1000);
                        }
                    }while (selection < 1 || selection > 2);
                    outs++; break;
                case 0: // End Inning
                    inningOver = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    delay(1000);
                    break;
            }
            delay(2000);
            System.out.println("-------------------------------");
            gameLog.add("---------------------");
            if(outs == 3){
                inningOver = true;
                break;
            }
        }
    }

    private String getRandomStrikeOutMessage() {
        Random rand = new Random();
        int min = 0;
        int max = 5;
        int randomNum = rand.nextInt((max-min) + 1) + min;
        switch (randomNum){
            case 0: return "Strike three! Another batter down.";
            case 1: return "And that's a strikeout! Impressive pitching.";
            case 2: return "He's out! What a fantastic pitch to end the at-bat.";
            case 3: return "The pitcher racks up another K on the board.";
            case 4: return "And the batter heads back to the dugout, another strikeout for the pitcher.";
            case 5: return "The pitcher closes the at-bat with a commanding strikeout.";
        }
        return "Struck him out!";
    }

    private PlayerList setBattingOrder(Team team){
        System.out.println("Setting the batting order for " + team.getName());
        PlayerList roster = team.getRoster();
        PlayerList battingOrder = new PlayerList();
        System.out.println("How many batters in the lineup?");
        int battersInLineup = input.nextInt();
        input.nextLine();

        for(int i=1;i<=battersInLineup+1;i++){
            boolean validPlayer = false;
            int jerseyNumber = -1;
            roster.statsTable(0);
            while(!validPlayer){
                if(i<=battersInLineup){
                    try{
                        System.out.print("Enter the jersey number for batting position " + i + ": ");
                        jerseyNumber = input.nextInt();
                        input.nextLine(); // Consume newline left-over
                    }catch (NoSuchElementException e) {
                        System.out.println("No player with jersey number " + jerseyNumber + " found. Please try again: ");
                        jerseyNumber = input.nextInt();
                        input.nextLine();
                    }
                }else {
                    roster.statsTable(1);
                    System.out.print("Enter the jersey number for your starting pitcher today: ");
                    try{
                        jerseyNumber = input.nextInt();
                        input.nextLine();
                        this.currentPitcher = roster.getPlayerByJerseyNumber(jerseyNumber);
                        System.out.println("Lineup set for " + team.getName());
                        delay(1000);
                        return battingOrder;
                    }catch (NoSuchElementException e) {
                        System.out.println("No player with jersey number " + jerseyNumber + " found. Please try again: ");
                        delay(2000);
                        jerseyNumber = input.nextInt();
                        input.nextLine();
                    }
                }


                try {
                    Player player = roster.getPlayerByJerseyNumber(jerseyNumber);
                    if (player.getOrderinLineup() == 0) { // Check if the player is not already in the batting order
                        player.setOrderinLineup(i);
                        battingOrder.addPlayer(player); // Add to batting order if maintaining a separate list
                        validPlayer = true;
                        System.out.println(player.getName() + " set to batting position " + i);
                        delay(500);
                    } else {
                        System.out.println("This player has already been assigned a batting position. Please choose another player.");
                    }
                } catch (NoSuchElementException e) {
                    System.out.println("No player with jersey number " + jerseyNumber + " found. Please try again.");
                }
            }
        }
        System.out.println("Lineup set for " + team.getName());
        delay(1000);
        return battingOrder;
    }

    public void pinchHitter(Map<Player, Map<String, Integer>> gameStats, PlayerList lineup, int currentBatterIndex, Player playerToSubIn) {

        lineup.substitutePlayer(currentBatterIndex, playerToSubIn);

        // Check if the new player already has stats in the map
        if (!gameStats.containsKey(playerToSubIn)) {
            Map<String, Integer> newPlayerStats = new HashMap<>();
            // Initialize stats for the new player
            newPlayerStats.put("Hits", 0);
            newPlayerStats.put("AtBats", 0);
            newPlayerStats.put("HomeRuns", 0);
            newPlayerStats.put("RBIs", 0);
            //pitching
            newPlayerStats.put("InningsPitched", 0);
            newPlayerStats.put("HitsAllowed", 0);
            newPlayerStats.put("Walks", 0);
            newPlayerStats.put("StrikeOuts", 0);
            gameStats.put(playerToSubIn, newPlayerStats);
        }

        // The substituted player's stats remain in the map
    }

    //for tracking in-game stats
    public void recordHit(Player player, int rbiCount) {
        if (gameStats.containsKey(player)) {
            Map<String, Integer> stats = gameStats.get(player);
            stats.put("Hits", stats.get("Hits") + 1);
            stats.put("AtBats", stats.get("AtBats") + 1);
            stats.put("RBIs", stats.get("RBIs") + rbiCount);
        }
    }

    public void recordHomeRun(Player player, int rbiCount) {
        if (gameStats.containsKey(player)) {
            Map<String, Integer> stats = gameStats.get(player);
            stats.put("HomeRuns", stats.get("HomeRuns") + 1);
            stats.put("RBIs", stats.get("RBIs") + rbiCount);
            stats.put("Hits", stats.get("Hits") + 1);
            stats.put("AtBats", stats.get("AtBats") + 1);
        }
    }

    public void recordStrikeOut(Player pitcher) {
        if (gameStats.containsKey(pitcher)) {
            Map<String, Integer> stats = gameStats.get(pitcher);
            stats.put("StrikeOuts", stats.get("StrikeOuts") + 1);
        }
    }

    public void recordInningPitched(Player pitcher) {
        if (gameStats.containsKey(pitcher)) {
            Map<String, Integer> stats = gameStats.get(pitcher);
            stats.put("InningsPitched", stats.get("InningsPitched") + 1);
        }
    }

    private void delay(long milliseconds){
        try {
            Thread.sleep(milliseconds); // 1000 milliseconds = 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Handle the interruption
        }
    }

    public void exportGameLog() throws IOException {
        // Ensure the directory exists
        File directory = new File(gameLogDirectory).getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create directory: " + directory.getPath());
        }

        // Now write to the file
        try (FileWriter writer = new FileWriter(gameLogDirectory)) {
            // Export game stats
            for (Map.Entry<Player, Map<String, Integer>> entry : gameStats.entrySet()) {
                Player player = entry.getKey();
                Map<String, Integer> stats = entry.getValue();
                String statsLine = player.getName() + "," + stats.entrySet().stream()
                        .map(stat -> stat.getKey() + ": " + stat.getValue())
                        .collect(Collectors.joining(", "));
                writer.write(statsLine + "\n");
            }

            // Add a separator
            writer.write("\n--- Game Log ---\n");

            // Export game log
            for (String logEntry : gameLog) {
                writer.write(logEntry + "\n");
            }
        }
    }



}
