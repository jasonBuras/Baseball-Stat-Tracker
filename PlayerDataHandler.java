package Baseball

import java.io.*;

public class PlayerDataHandler {

    public PlayerList importPlayers(Team team) {
        String directoryPath = "data/teams/" + team.getFilePath();
        String filePath = directoryPath + "/playerlist.csv";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs(); // This will create the directory if it doesn't exist
        }

        File file = new File(filePath);
        if(!file.exists()){
            try{
                file.createNewFile();
                try(PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))){
                    // Updated CSV header
                    pw.println("JerseyNumber,FirstName,LastName,AtBats,Singles,Doubles,Triples,HomeRuns,RBIs,OutsRecorded,PitchCount,Strikes,Balls,HitsGivenUp,WalksGivenUp,Strikeouts,BattersHit");

                    return new PlayerList();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        PlayerList players = new PlayerList();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {
            String line;
            br.readLine(); // Skip the header line
            while ((line = br.readLine()) != null) {
                String[] playerData = line.split(",");
                if (playerData.length >= 17) { // Ensure that there are enough data fields (N+1)
                    // Parse the player data from the CSV
                    int jerseyNumber = parseOrDefault(playerData[0], 0);
                    String firstName = playerData[1].trim();
                    String lastName = playerData[2].trim();
                    int atBats = parseOrDefault(playerData[3], 0);
                    int singles = parseOrDefault(playerData[4], 0);
                    int doubles = parseOrDefault(playerData[5], 0);
                    int triples = parseOrDefault(playerData[6], 0);
                    int homeRuns = parseOrDefault(playerData[7], 0);
                    int RBIs = parseOrDefault(playerData[8], 0);
                    int outsRecorded = parseOrDefault(playerData[9], 0);
                    int pitchCount = parseOrDefault(playerData[10], 0);
                    int strikes = parseOrDefault(playerData[11], 0);
                    int balls = parseOrDefault(playerData[12], 0);
                    int hitsGivenUp = parseOrDefault(playerData[13], 0);
                    int walksGivenUp = parseOrDefault(playerData[14], 0);
                    int strikeouts = parseOrDefault(playerData[15], 0);
                    int battersHit = parseOrDefault(playerData[16], 0);

                    // Create a new Player object with the imported data
                    Player player = new Player(firstName, lastName, jerseyNumber,atBats,singles,doubles,triples,
                                               homeRuns,RBIs,outsRecorded,pitchCount,strikes,balls,hitsGivenUp,
                                               walksGivenUp,strikeouts,battersHit);
                    // Set the stats for the player
                    // ... (Set the new stats on the player object here)
                    // Add the player to the list
                    players.addPlayer(player);
                }
            }
        } catch (FileNotFoundException e) {
            // If the file doesn't exist, we can create it later when we export
        } catch (IOException e) {
            e.printStackTrace();
        }
        return players;
    }

    private int parseOrDefault(String numberString, int defaultValue) {
        try {
            return Integer.parseInt(numberString.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public void exportPlayers(Team team, PlayerList players) throws IOException {
        String directoryPath = "data/teams/" + team.getFilePath();
        String filePath = directoryPath + "/playerlist.csv";
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile(); // This will create the file if it doesn't exist
        }

        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"))) {
            // Updated CSV header
            pw.println("JerseyNumber,FirstName,LastName,AtBats,Singles,Doubles,Triples,HomeRuns,RBIs,OutsRecorded,PitchCount,Strikes,Balls,HitsGivenUp,WalksGivenUp,Strikeouts,BattersHit");
            // Write the player data
            for (Player player : players.getPlayers()) {
                // Updated format string to include new stats
                pw.printf("%d,%s,%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d%n",
                        player.getJerseyNumber(),
                        player.getFirstName(),
                        player.getLastName(),
                        player.getStats().getAtBats(),
                        player.getStats().getSingles(),
                        player.getStats().getDoubles(),
                        player.getStats().getTriples(),
                        player.getStats().getHomeRuns(),
                        player.getStats().getRBIs(),
                        player.getStats().getOutsRecorded(),
                        player.getStats().getpitchCount(),
                        player.getStats().getStrikes(),
                        player.getStats().getBalls(),
                        player.getStats().getHitsGivenUp(),
                        player.getStats().getWalksGivenUp(),
                        player.getStats().getStrikeouts(),
                        player.getStats().getBattersHit()
                        // ... (Write the new stats of the player object here)
                );
            }
        }catch (IOException e) {
            // Handle the IOException here
            System.err.println("An error occurred while exporting players: " + e.getMessage());
        }
    }
}