package CLI;

import Core.ChampionshipManager;
import Core.Team;
import Core.Player;

import java.util.Scanner;

/**
 * CLI.MainCLI: Command-line interface for the Football Championship.
 * Supports recording matches with scorer, optional assister, goalkeeper saves, and points.
 */
public class MainCLI {
    private final ChampionshipManager manager;
    private final Scanner scanner;

    public MainCLI(ChampionshipManager manager) {
        this.manager = manager;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Main loop: list, add teams/players, record matches, or exit.
     */
    public void run() {
        while (true) {
            System.out.println("\n--- Football Championship (CLI) ---");
            System.out.println("1) List teams & players");
            System.out.println("2) Add team");
            System.out.println("3) Add player to team");
            System.out.println("4) Record match");
            System.out.println("5) Exit");
            System.out.print("Choose> ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println(manager.listAll());
                    break;
                case "2":
                    addTeam();
                    break;
                case "3":
                    addPlayer();
                    break;
                case "4":
                    recordMatch();
                    break;
                case "5":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void addTeam() {
        System.out.print("Team name> ");
        String name = scanner.nextLine().trim();
        if (manager.findTeam(name) != null) {
            System.out.println("Team already exists.");
        } else {
            manager.addTeam(new Team(name));
            System.out.println("Added team: " + name);
        }
    }

    private void addPlayer() {
        System.out.print("Team name> ");
        Team team = manager.findTeam(scanner.nextLine().trim());
        if (team == null) {
            System.out.println("Team not found.");
            return;
        }
        System.out.print("Player name> ");
        String playerName = scanner.nextLine().trim();
        System.out.print("Position> ");
        String position = scanner.nextLine().trim();
        team.addPlayer(new Player(playerName, position));
        System.out.println("Added " + position + " " + playerName + " to " + team.getName());
    }

    /**
     * Records a match: prompts for scorers, assisters, goalkeeper saves, updates stats and awards points.
     */
    private void recordMatch() {
        // Select teams
        System.out.print("Home team> ");
        Team home = manager.findTeam(scanner.nextLine().trim());
        if (home == null) { System.out.println("Team not found."); return; }
        System.out.print("Away team> ");
        Team away = manager.findTeam(scanner.nextLine().trim());
        if (away == null) { System.out.println("Team not found."); return; }

        // Enter scoreline
        System.out.print("Home goals scored> ");
        int hg = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Away goals scored> ");
        int ag = Integer.parseInt(scanner.nextLine().trim());

        // Home goals: scorer + optional assister
        for (int i = 1; i <= hg; i++) {
            System.out.print("Home scorer #" + i + "> ");
            Player s = manager.findPlayer(scanner.nextLine().trim());
            System.out.print("Assister for this goal (or press Enter for none)> ");
            String an = scanner.nextLine().trim();
            Player a = an.isEmpty() ? null : manager.findPlayer(an);
            if (s != null) s.updateStats(1, 0, 0, 0);
            if (a != null && a != s) a.updateStats(0, 1, 0, 0);
        }

        // Away goals
        for (int i = 1; i <= ag; i++) {
            System.out.print("Away scorer #" + i + "> ");
            Player s = manager.findPlayer(scanner.nextLine().trim());
            System.out.print("Assister for this goal (or press Enter for none)> ");
            String an = scanner.nextLine().trim();
            Player a = an.isEmpty() ? null : manager.findPlayer(an);
            if (s != null) s.updateStats(1, 0, 0, 0);
            if (a != null && a != s) a.updateStats(0, 1, 0, 0);
        }

        // Goalkeeper stats
        System.out.println("--- Record goalkeeper stats ---");
        for (Player p : home.getPlayers()) {
            if ("Goalkeeper".equalsIgnoreCase(p.getPosition())) {
                System.out.print("Saves by " + p.getName() + "> ");
                int sv = Integer.parseInt(scanner.nextLine().trim());
                p.updateStats(0, 0, sv, ag);
            }
        }
        for (Player p : away.getPlayers()) {
            if ("Goalkeeper".equalsIgnoreCase(p.getPosition())) {
                System.out.print("Saves by " + p.getName() + "> ");
                int sv = Integer.parseInt(scanner.nextLine().trim());
                p.updateStats(0, 0, sv, hg);
            }
        }

        // Recalculate team stats
        home.recalculateStats();
        away.recalculateStats();

        // Award points based on result
        manager.recordMatchPoints(home, away, hg, ag);

        System.out.println("Match recorded: "
                + home.getName() + " " + hg + " - " + ag + " " + away.getName());
    }
}
