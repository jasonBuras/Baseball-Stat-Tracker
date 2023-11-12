package Baseball

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class PlayerList implements Serializable {
    private List<Player> players;

    public PlayerList() {
        this.players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public Player getPlayerByFirstName(String firstName) {
        for (Player player : players) {
            if (player.getFirstName().equalsIgnoreCase(firstName)) {
                return player;
            }
        }
        throw new NoSuchElementException("A player with the first name '" + firstName + "' does not exist."); // or throw an exception if preferred
    }

    public Player getPlayerByIndex(int index){
        return players.get(index);
    }

    public Player get(int i) {
        return players.get(i);
    }

    public Player getPlayerByLastName(String lastName) {
        for (Player player : players) {
            if (player.getLastName().equalsIgnoreCase(lastName)) {
                return player;
            }
        }
        throw new NoSuchElementException("A player with the last name '" + lastName + "' does not exist."); // or throw an exception if preferred
    }

    public Player getPlayerByJerseyNumber(int jerseyNumber) {
        for (Player player : players) {
            if (player.getJerseyNumber() == jerseyNumber) {
                return player;
            }
        }
        throw new NoSuchElementException("A player with the number '" + jerseyNumber + "' does not exist."); // or throw an exception if preferred
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int size(){
        return players.size();
    }

    @Override
    public String toString() {
        StringBuilder playerData = new StringBuilder();
        for (Player player : players) {
            playerData.append("#").append(player.getJerseyNumber()).append(" ")
                    .append(player.getFirstName()).append(" ")
                    .append(player.getLastName()).append("\n");
        }
        return playerData.toString();//yes.. use .toString in a .toString() method
    }

    public void statsTable(int select){
        if(select==0){
            System.out.printf("%-4s %-20s %-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s%n",
                    "#", "Name", "AVG", "AB", "1B", "2B", "3B", "HR", "RBI", "BB", "OBP", "SLG");
            for (Player player: players) {
                player.printHittingStats(false);
            }
        }else{
            System.out.printf("%-4s %-20s %-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s%n",
                    "#", "Name", "IP", "PC", "ST", "B", "H", "BB", "SO", "WHIP");
            for (Player player: players) {
                player.printPitchingStats(false);
            }
        }
    }

    public Player findPlayer(String firstName, String lastName, int jerseyNumber) {
        for (Player player : players) {
            if (player.getFirstName().equalsIgnoreCase(firstName) &&
                    player.getLastName().equalsIgnoreCase(lastName) &&
                    player.getJerseyNumber() == jerseyNumber) {
                return player;
            }
        }
        throw new NoSuchElementException("No such player (" + lastName + ") exists.");
    }

    /*
    VERY IMPORTANT: Recommend only to use substitutePlayer for the lineup PlayerList.
    Using it on the roster PlayerList is destructive and will overwrite player data.
    lineup PlayerList is just a temporary list that fetches data from the roster PlayerList
     */
    public void substitutePlayer(int index, Player newPlayer) {
        // Check if the index is within the range of the players list
        if (index >= 0 && index < players.size()) {
            // Replace the player at the index with the new player
            players.set(index, newPlayer);
        } else {
            // Handle the case where the index is out of bounds
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + players.size());
        }
    }

    public PlayerList search(String lastname){
        PlayerList candidates = new PlayerList();
        for(Player player : players){
            if(player.getLastName().equals(lastname)){
                candidates.addPlayer(player);
            }
        }
        return candidates;//quick thought: if candidates.size == 1, then one player matches. Just return their stat table
    }

    public PlayerList search(String lastname, String firstname){
        PlayerList candidates = new PlayerList();
        for(Player player : players){
            if(player.getLastName().equals(lastname) && player.getFirstName().equals(firstname)){
                candidates.addPlayer(player);
            }
        }
        return candidates;
    }

    public PlayerList search(String lastname, String firstname, int jerseynumber){
        PlayerList candidates = new PlayerList();
        for(Player player : players){
            if(player.getLastName().equals(lastname) && player.getFirstName().equals(firstname) && player.getJerseyNumber() == jerseynumber){
                candidates.addPlayer(player);
            }
        }
        return candidates;
    }

    public PlayerList search(String lastname, int jerseynumber){
        PlayerList candidates = new PlayerList();
        for(Player player : players){
            if(player.getLastName().equals(lastname) && player.getJerseyNumber() == jerseynumber){
                candidates.addPlayer(player);
            }
        }
        return candidates;
    }



}