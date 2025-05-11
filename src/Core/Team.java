package Core;

import java.util.ArrayList;
import java.util.List;

/**
 * Core.Team: Represents a football team containing players, tracking stats and points.
 */
public class Team {
    private String name;
    private List<Player> players;
    private int goalsFor;
    private int goalsAgainst;
    private int points; // tournament points: 3 for win, 1 for draw, 0 for loss

    /**
     * Constructs a new Team with the given name.
     * @param name Team's name
     */
    public Team(String name) {
        this.name = name;
        this.players = new ArrayList<>();
        this.goalsFor = 0;
        this.goalsAgainst = 0;
        this.points = 0;
    }

    // Getters
    public String getName() { return name; }
    public List<Player> getPlayers() { return players; }
    public int getGoalsFor() { return goalsFor; }
    public int getGoalsAgainst() { return goalsAgainst; }
    public int getPoints() { return points; }

    /**
     * Adds points to this team (3 win, 1 draw, 0 loss).
     * @param pts Number of points to add
     */
    public void addPoints(int pts) {
        this.points += pts;
    }

    /**
     * Adds a player to this team's roster.
     * @param player Player to add
     */
    public void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * Finds a player by name (case-insensitive).
     * @param playerName Name to search
     * @return Player if found, otherwise null
     */
    public Player findPlayer(String playerName) {
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(playerName)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Recalculates aggregate stats from all players.
     * Call after any match updates to refresh GF and GA.
     */
    public void recalculateStats() {
        goalsFor = 0;
        goalsAgainst = 0;
        for (Player p : players) {
            goalsFor += p.getGoals();
            if ("Goalkeeper".equalsIgnoreCase(p.getPosition())) {
                goalsAgainst += p.getGoalsConceded();
            }
        }
    }

    @Override
    public String toString() {
        recalculateStats(); // ensure stats up to date
        // Format: TeamName [Pts:X] GS:Y GA:Z
        return String.format("%s [Pts:%d] GS:%d GA:%d",
                name, points, goalsFor, goalsAgainst);
    }
}
