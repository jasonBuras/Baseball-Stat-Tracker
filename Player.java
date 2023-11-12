package Baseball

import java.io.Serializable;
import java.util.Objects;

public class Player implements Serializable {

    private String firstName;
    private String lastName;
    private int jerseyNumber;
    private int orderinLineup;
    private Stats stats;
    private Team team;
    private String playerID;
    //future: private boolean isPitcher;

    public Player(String firstName, String lastName, int jerseyNumber){
        this.firstName = firstName;
        this.lastName = lastName;
        this.jerseyNumber = jerseyNumber;
        this.stats = new Stats();
        this.orderinLineup = 0;
        this.team = null;
    }

    public Player(String firstName, String lastName, int jerseyNumber, int atBats, int singles, int doubles, int triples, int homeRuns, int RBIs, int outsRecorded, int pitchCount, int strikes, int balls, int hitsGivenUp, int walksGivenUp, int strikeouts, int battersHit) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.jerseyNumber = jerseyNumber;
        this.stats = new Stats();
        this.orderinLineup = 0;
        this.team = null;
        this.getStats().setAtBats(atBats);
        this.getStats().setSingles(singles);
        this.getStats().setDoubles(doubles);
        this.getStats().setTriples(triples);
        this.getStats().setHomeRuns(homeRuns);
        this.getStats().setRBIs(RBIs);
        this.getStats().setOutsRecorded(outsRecorded);
        this.getStats().setpitchCount(pitchCount);
        this.getStats().setStrikes(strikes);
        this.getStats().setBalls(balls);
        this.getStats().setHitsGivenUp(hitsGivenUp);
        this.getStats().setWalksGivenUp(walksGivenUp);
        this.getStats().setStrikeouts(strikeouts);
        this.getStats().setBattersHit(battersHit);
        this.playerID = firstName+lastName+jerseyNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getJerseyNumber(){
        return jerseyNumber;
    }

    public void setJerseyNumber(int jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }

    public int getOrderinLineup() {
        return orderinLineup;
    }

    public void setOrderinLineup(int orderinLineup) {
        this.orderinLineup = orderinLineup;
    }

    public void setTeam(Team team){
        this.team = team;
        this.playerID = firstName+lastName+jerseyNumber+ getTeamName().substring(0,3).toUpperCase();
    }

    public Team getTeam() {
        return team;
    }

    public String getTeamName() {
        return team.getName();
    }

    // Delegate methods to interact with the stats tracker
    public void addAtBat(int atBats) {
        stats.addAtBat(atBats);
    }

    public void addHit(int hits) {
        stats.addHit(hits);
    }

    public void addBoB(int walks) {
        stats.addBoB(walks);
    }
    public void addHBP(int HBP){
        stats.addHBP(HBP);
    }

    public void addHomeRun(int homeRuns) {
        stats.addHomeRun(homeRuns);
    }

    public double getBattingAverage() {
        return stats.calculateBattingAverage();
    }

    public double getOnBasePercentage() {
        return stats.calculateOnBasePercentage();
    }

    public double getSluggingPercentage() {
        return stats.calculateSluggingPercentage();
    }

    //Pitching Stats
    public void addPitch(boolean isStrike) {
        stats.addPitch(isStrike);
    }

    public void addPitch() {
        stats.addPitch();
    }

    public void addHitGivenUp(boolean isHit){
        stats.addHitGivenUp(isHit);
    }

    public void addOutRecorded() {
        stats.addOutRecorded();
    }

    public void addStrikeout(int strikeouts) {
        stats.addStrikeout(strikeouts);
    }

    public Stats getStats() {
        return stats;
    }

    public void resetStats(boolean AREYOUSURE){//BE VERY CAREFUL
        if(AREYOUSURE){
            stats = new Stats();
        }else{
            System.out.println("Close one");
        }
    }

    public void setStats(Stats stats){
        this.stats = stats;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public void addHit(int bases, int RBIs) {
        stats.addHit(bases, RBIs);
    }

    public void addDouble(int RBIs) {
        stats.addDouble(RBIs);
    }

    public void addTriple(int RBIs){
        stats.addTriple(RBIs);
    }

    public void addRBI(int RBIs){
        stats.addRBI(RBIs);
    }


    public void addWalk(int walksGivenUp) {
        stats.addWalk(walksGivenUp);
    }

    public void addHitBatter(int battersHit) {
        stats.addHitBatter(battersHit);
    }

    public void error() {
        stats.error();
    }
    public void printHittingStats(boolean individual){
        if(individual){
            //print header
            System.out.printf("%-4s %-20s %-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s%n",
                    "Jersey", "Name", "AVG", "AB", "1B", "2B", "3B", "HR", "RBI", "BB", "OBP", "SLG");
        }
        System.out.printf("%-4d %-20s %-10.3f %-10d %-10d %-10d %-10d %-10d %-10d %-10d %-10.3f %-10.3f%n",
                jerseyNumber,
                firstName + " " + lastName,
                getBattingAverage(),
                stats.getAtBats(),
                stats.getSingles(),
                stats.getDoubles(),
                stats.getTriples(),
                stats.getHomeRuns(),
                stats.getRBIs(),
                stats.getBoB(),
                getOnBasePercentage(),
                getSluggingPercentage());
    }

    public void printPitchingStats(boolean individual){
        if(individual){
            //print header
            System.out.printf("%-4s %-20s %-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s%n",
                    "Jersey", "Name", "IP", "PC", "ST", "B", "H", "BB", "SO", "WHIP");
        }
        System.out.printf("%-4d %-20s %-10.1f %-10d %-10d %-10d %-10d %-10d %-10d %-10.3f%n",
                jerseyNumber,
                firstName + " " + lastName,
                stats.getInningsPitched(),
                stats.getpitchCount(),
                stats.getStrikes(),
                stats.getBalls(),
                stats.getHitsGivenUp(),
                stats.getWalksGivenUp(),
                stats.getStrikeouts(),
                stats.calculateWHIP());
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return getJerseyNumber() == player.getJerseyNumber() &&
                Objects.equals(getFirstName(), player.getFirstName()) &&
                Objects.equals(getLastName(), player.getLastName()) &&
                Objects.equals(getTeam(), player.getTeam());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getJerseyNumber(), getTeam());
    }

    public static class Stats implements Serializable {
        private int atBats;
        private int hits;
        private int BoB;

        private int HBP; //hit by pitch
        private int RBIs;
        private int singles;
        private int doubles;
        private int triples;
        private int homeRuns;

        //pitching
        private int outsRecorded; // For calculating innings pitched
        private int pitchCount;
        private int strikes;
        private int balls;
        private int hitsGivenUp;
        private int walksGivenUp;
        private int strikeouts;
        private int battersHit;

        public Stats() {
            //hitter
            this.atBats = 0;
            this.hits = 0;
            this.BoB = 0;
            this.homeRuns = 0;
            this.HBP = 0;
            this.RBIs = 0;
            this.singles = 0;
            this.doubles = 0;
            this.triples = 0;

            //pitcher
            this.pitchCount = 0;
            this.hitsGivenUp = 0;
            this.strikes = 0;
            this.balls = 0;
            this.strikeouts = 0;
            this.walksGivenUp = 0;
            this.outsRecorded = 0;
            this.battersHit = 0;

        }

        //hitting
        public void addHit(int bases, int RBIs) {
            this.atBats++;
            this.RBIs += RBIs;
            switch (bases){
                case 1: this.singles++; break;
                case 2: this.doubles++; break;
                case 3: this.triples++; break;
                case 4: this.homeRuns++; break;
            }
            this.singles++;
            this.atBats++;
            this.RBIs += RBIs;
        }

        public void addDouble(int RBIs){
            this.doubles++;
            this.atBats++;
            this.RBIs += RBIs;
        }

        public void addTriple(int RBIs){
            this.triples++;
            this.RBIs += RBIs;
        }

        public void addRBI(int RBIs){
            this.RBIs += RBIs;
        }

        public void addHomeRun() {
            this.homeRuns++;
        }

        public void addAtBat(int atBats) {
            this.atBats += atBats;
        }

        public void addHBP(int HBP){
            this.HBP += HBP;
        }

        public void addHit(int hits) {
            this.hits += hits;
        }

        public void addBoB(int walks) {
            this.BoB += walks;
        }

        public void addHomeRun(int homeRuns) {
            this.homeRuns += homeRuns;
        }

        public double calculateBattingAverage() {
            return (this.singles + this.doubles + this.triples + this.homeRuns) / (double) this.atBats;
        }

        public double calculateOnBasePercentage() {
            return (this.hits + this.BoB) / (double) (this.atBats + this.BoB);
        }

        public double calculateSluggingPercentage() {
            // Assuming a single hit is a single, double is two bases, etc.
            // This method would need more data to accurately calculate slugging percentage.
            return ((this.singles + (this.doubles*2) + (this.triples*3) + (this.homeRuns*4)) / ((double) this.atBats));
        }

        //Pitching
        public void addPitch(boolean isStrike) {
            this.pitchCount++;
            if (isStrike) {
                this.strikes++;
            } else {
                this.balls++;
            }
        }

        public void addPitch(){
            this.pitchCount++;
        }

        public void addOutRecorded() {
            this.outsRecorded++;
        }

        public void addHitGivenUp(boolean isHit){
            this.pitchCount++;
            if(isHit){
                this.hitsGivenUp++;
            }
        }

        public void addStrikeout() {
            this.strikeouts++;
            addOutRecorded();
        }

        public void addWalk() {
            this.walksGivenUp++;
        }

        public void addWalk(int walksGivenUp) {
            this.walksGivenUp += walksGivenUp;
        }

        public void addHitBatter(int hitBatters) {
            this.battersHit += hitBatters;
        }

        public void addStrikeout(int strikeOuts) {
            this.strikeouts += strikeOuts;
            this.outsRecorded += strikeOuts;
        }

        public void error() {
            this.pitchCount++;
        }

        public double getInningsPitched() {
            int fullInnings = this.outsRecorded / 3; // Calculate the full innings
            int partialInnings = this.outsRecorded % 3; // Calculate the remaining outs

            // Convert the partial innings to the convention (.1 for one out, .2 for two outs)
            double innings = fullInnings;
            if (partialInnings == 1) {
                innings += 0.1;
            } else if (partialInnings == 2) {
                innings += 0.2;
            }
            return innings;
        }

        /*
        According to MLB standards (just here for reference and to determine whether or not it's worth even keeping track of)
        Excellent	    1.00
        Great	        1.10
        Above Average	1.20
        Average	        1.30
        Below Average	1.40
        Poor	        1.50
        Awful	        1.60
         */
        public double calculateWHIP() {
            return (this.walksGivenUp + this.hitsGivenUp) / getInningsPitched();
        }

        public void setAtBats(int atBats) {
            this.atBats = atBats;
        }

        public void setHits(int hits) {
            this.hits = hits;
        }

        public void setBoB(int boB) {
            this.BoB = boB;
        }

        public void setHomeRuns(int homeRuns) {
            this.homeRuns = homeRuns;
        }

        public void setOutsRecorded(int outsRecorded) {
            this.outsRecorded = outsRecorded;
        }

        public void setpitchCount(int pitchCount) {
            this.pitchCount = pitchCount;
        }

        public void setStrikes(int strikes) {
            this.strikes = strikes;
        }

        public void setBalls(int balls) {
            this.balls = balls;
        }

        public void setStrikeouts(int strikeouts) {
            this.strikeouts = strikeouts;
        }

        public void setWalksPitched(int walksPitched) {
            this.walksGivenUp = walksPitched;
        }

        public int getAtBats() {
            return atBats;
        }

        public int getHits() {
            return hits;
        }

        public int getBoB() {
            return BoB;
        }

        public int getHomeRuns() {
            return homeRuns;
        }

        public int getHBP() {
            return HBP;
        }

        public int getRBIs() {
            return RBIs;
        }

        public int getSingles() {
            return singles;
        }

        public int getDoubles() {
            return doubles;
        }

        public int getTriples() {
            return triples;
        }


        //pitcher stats
        public int getpitchCount() {
            return pitchCount;
        }

        public int getStrikes() {
            return strikes;
        }

        public int getBalls() {
            return balls;
        }

        public int getStrikeouts() {
            return strikeouts;
        }

        public int getWalksGivenUp() {
            return walksGivenUp;
        }

        public int getOutsRecorded() {
            return outsRecorded;
        }

        public void setHBP(int HBP) {
            this.HBP = HBP;
        }

        public void setRBIs(int RBIs) {
            this.RBIs = RBIs;
        }

        public void setSingles(int singles) {
            this.singles = singles;
        }

        public void setDoubles(int doubles) {
            this.doubles = doubles;
        }

        public void setTriples(int triples) {
            this.triples = triples;
        }

        public int getPitchCount() {
            return pitchCount;
        }

        public void setPitchCount(int pitchCount) {
            this.pitchCount = pitchCount;
        }

        public int getHitsGivenUp() {
            return hitsGivenUp;
        }

        public void setHitsGivenUp(int hitsGivenUp) {
            this.hitsGivenUp = hitsGivenUp;
        }

        public int getBattersHit() {
            return battersHit;
        }

        public void setBattersHit(int battersHit) {
            this.battersHit = battersHit;
        }

        public void setWalksGivenUp(int walksGivenUp) {
            this.walksGivenUp = walksGivenUp;
        }

        // Additional methods

    }

}
