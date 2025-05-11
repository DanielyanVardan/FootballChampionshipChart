package Core;

/**
 * Core.Player: Represents a football player with stats, including goalkeeper-specific stats.
 * toString now uses full stat names (Goals, Assists, Saves, Conceded).
 */
public class Player {
    private String name;
    private String position;  // e.g., "Forward", "Midfielder", "Defender", "Goalkeeper"
    private int goals;
    private int assists;
    // Goalkeeper-specific stats
    private int saves;
    private int goalsConceded;

    /**
     * Constructs a new Player.
     * @param name      the player's name
     * @param position  the player's position ("Goalkeeper" for keepers)
     */
    public Player(String name, String position) {
        this.name = name;
        this.position = position;
        this.goals = 0;
        this.assists = 0;
        this.saves = 0;
        this.goalsConceded = 0;
    }

    // Getters
    public String getName() { return name; }
    public String getPosition() { return position; }
    public int getGoals() { return goals; }
    public int getAssists() { return assists; }
    public int getSaves() { return saves; }
    public int getGoalsConceded() { return goalsConceded; }

    /**
     * Updates this player's statistics.
     * @param goalsToAdd        number of goals to add
     * @param assistsToAdd      number of assists to add
     * @param savesToAdd        number of saves to add (only applies if goalkeeper)
     * @param concededToAdd     number of goals conceded (only applies if goalkeeper)
     */
    public void updateStats(int goalsToAdd, int assistsToAdd, int savesToAdd, int concededToAdd) {
        this.goals += goalsToAdd;
        this.assists += assistsToAdd;
        if ("Goalkeeper".equalsIgnoreCase(position)) {
            this.saves += savesToAdd;
            this.goalsConceded += concededToAdd;
        }
    }

    @Override
    public String toString() {
        String base = String.format(
                "%s (%s)  Goals:%d  Assists:%d",
                name, position, goals, assists
        );
        if ("Goalkeeper".equalsIgnoreCase(position)) {
            return String.format(
                    "%s  Saves:%d  Conceded:%d",
                    base, saves, goalsConceded
            );
        }
        return base;
    }
}
