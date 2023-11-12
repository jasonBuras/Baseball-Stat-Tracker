package Baseball

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;

public class Team implements Serializable {
    private String name;
    private int ageGroup;
    private PlayerList roster;
    private PlayerList lineup;

    public Team(String name, int ageGroup) {
        this.name = name;
        this.ageGroup = ageGroup;
        this.roster = new PlayerList();
        this.lineup = new PlayerList();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(int ageGroup) {
        this.ageGroup = ageGroup;
    }

    //Player Management
    public PlayerList getRoster() {
        return roster;
    }

    public PlayerList getLineup() {
        return lineup;
    }

    public void setLineup(PlayerList lineup) {
        this.lineup = lineup;
    }

    public void addPlayer(Player player){
        player.setTeam(this);
        roster.addPlayer(player);
    }

    public void addPlayer(String firstName, String lastName, int jerseyNumber) {
        Player newPlayer = new Player(firstName, lastName, jerseyNumber);
        this.roster.addPlayer(newPlayer);
    }

    public void setRoster(PlayerList roster) {
        this.roster = roster;
    }

    public boolean removePlayer(String firstName, String lastName, int jerseyNumber) {
        for (Iterator<Player> iterator = roster.getPlayers().iterator(); iterator.hasNext(); ) {
            Player player = iterator.next();
            if (player.getFirstName().equalsIgnoreCase(firstName) &&
                    player.getLastName().equalsIgnoreCase(lastName) &&
                    player.getJerseyNumber() == jerseyNumber) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public int getPlayerCount() {
        return roster.size();
    }

    public Player getPlayerByIndex(int i) {
        return roster.getPlayers().get(i);
    }

    public String getFilePath() {
        return name + ageGroup + "U";
    }

    public String gameLogPath(){
        return "Game Logs" + File.separator + getFilePath() +  File.separator + "GameLog-";//add date and .csv via Game class.
    }


    // Additional team-related methods can be added here
    public void resetStats(){
        for(Player player : roster.getPlayers()){
            player.resetStats(true);
        }
    }
}