package Baseball

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PersistenceManager {

    private final Path dataDirectoryPath = Paths.get("Data");

    public void saveTeam(Team team) {
        String baseFilename = team.getName();
        Path teamDataFilePath = dataDirectoryPath.resolve(baseFilename + ".ser");

        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(teamDataFilePath.toFile()))) {
            out.writeObject(team);
            System.out.println(team.getName() + "'s data saved.");
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception, possibly rethrow as a runtime exception or log it
        }
    }


    public Team loadTeam(String teamName) {
        Path teamDataFilePath = dataDirectoryPath.resolve(teamName + ".ser");
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(teamDataFilePath.toFile()))) {
            System.out.println(teamName + " loaded successfully");
            return (Team) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // Handle the exception, possibly return a new Team or null
        }
        return null; // Return null if there was an error
    }

    public List<Team> loadAllTeams() {
        List<Team> loadedTeams = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(dataDirectoryPath)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .forEach(path -> {
                        String fileName = path.getFileName().toString();
                        String teamName = fileName.substring(0, fileName.length() - 4); // Remove ".ser"
                        Team team = loadTeam(teamName);
                        if (team != null) {
                            System.out.println("Added " + team.getFilePath());
                            loadedTeams.add(team);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception, possibly log it
        }
        return loadedTeams;
    }

    @Deprecated
    private Path getUniqueFilePath(Path originalPath) {
        int counter = 1;
        Path filePath = originalPath;
        while (Files.exists(filePath)) {
            String fileName = String.format("%s%d.ser", originalPath.toString().replace(".ser", ""), counter);
            filePath = Paths.get(fileName);
            counter++;
        }
        return filePath;
    }
}