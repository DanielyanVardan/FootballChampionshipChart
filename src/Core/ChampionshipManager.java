package Core;

import java.util.ArrayList;
import java.util.List;

/**
 * Core.ChampionshipManager: Manages multiple teams in the championship,
 * awards points based on match results, and determines the competition winner.
 */
public class ChampionshipManager {
    private final List<Team> teams;
    private Team winner;
    private static final int POINTS_TO_WIN = 40;  // threshold to end competition

    /**
     * Constructs a new ChampionshipManager with no teams.
     */
    public ChampionshipManager() {
        this.teams = new ArrayList<>();
        this.winner = null;
    }

    /** Adds a team to the championship. */
    public void addTeam(Team team) {
        teams.add(team);
    }

    /** Returns all teams. */
    public List<Team> getTeams() {
        return teams;
    }

    /** Finds a team by name (case-insensitive). */
    public Team findTeam(String name) {
        for (Team t : teams) {
            if (t.getName().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }

    /** Finds a player across all teams by player name. */
    public Player findPlayer(String playerName) {
        for (Team t : teams) {
            Player p = t.findPlayer(playerName);
            if (p != null) return p;
        }
        return null;
    }

    /** Lists all teams and their rosters as a single string. */
    public String listAll() {
        StringBuilder sb = new StringBuilder();
        for (Team t : teams) {
            sb.append(t).append("\n");
            for (Player p : t.getPlayers()) {
                sb.append("  - ").append(p).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Awards tournament points based on match outcome and checks for a winner.
     * @param home     Home team
     * @param away     Away team
     * @param homeGoals Goals scored by home team
     * @param awayGoals Goals scored by away team
     */
    public void recordMatchPoints(Team home, Team away, int homeGoals, int awayGoals) {
        if (isCompetitionOver()) return;  // no further updates after winner

        if (homeGoals > awayGoals) {
            home.addPoints(3);
        } else if (homeGoals == awayGoals) {
            home.addPoints(1);
            away.addPoints(1);
        } else {
            away.addPoints(3);
        }
        // Check if threshold reached
        if (winner == null) {
            if (home.getPoints() >= POINTS_TO_WIN) {
                winner = home;
            } else if (away.getPoints() >= POINTS_TO_WIN) {
                winner = away;
            }
        }
    }

    /**
     * Returns true if a team has reached the winning threshold.
     */
    public boolean isCompetitionOver() {
        return winner != null;
    }

    /**
     * Returns the team that won, or null if competition is ongoing.
     */
    public Team getWinner() {
        return winner;
    }
}
