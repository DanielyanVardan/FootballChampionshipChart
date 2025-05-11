package GUI;

import Core.ChampionshipManager;
import Core.Team;
import Core.Player;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

/**
 * GUI.ChampionshipGUI: Swing-based graphical interface for the Football Championship.
 * Displays teams ordered by points, shows team and player stats,
 * supports recording matches with scorers, assisters, goalkeeper saves, points,
 * and displays a message when competition ends (first to reach threshold).
 */
public class ChampionshipGUI extends JFrame {
    private ChampionshipManager manager;
    private JLabel lblTeamStats;
    private JLabel lblPlayerStats;
    private JList<Team> teamList;
    private JList<Player> playerList;
    private DefaultListModel<Team> teamListModel;
    private DefaultListModel<Player> playerListModel;

    public ChampionshipGUI(ChampionshipManager manager) {
        this.manager = manager;
        initMenu();
        initComponents();
        setTitle("Football Championship");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Actions");
        JMenuItem miAddTeam = new JMenuItem("Add Team");
        miAddTeam.addActionListener(e -> { addTeam(); refreshTeamList(); });
        JMenuItem miAddPlayer = new JMenuItem("Add Player");
        miAddPlayer.addActionListener(e -> addPlayer());
        JMenuItem miRecordMatch = new JMenuItem("Record Match");
        miRecordMatch.addActionListener(e -> { recordMatch(); refreshTeamList(); });
        menu.add(miAddTeam);
        menu.add(miAddPlayer);
        menu.add(miRecordMatch);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new GridLayout(2,1));
        lblTeamStats = new JLabel("Select a team to see stats", SwingConstants.CENTER);
        lblTeamStats.setFont(lblTeamStats.getFont().deriveFont(Font.BOLD, 14f));
        lblPlayerStats = new JLabel("Select a player to see stats", SwingConstants.CENTER);
        topPanel.add(lblTeamStats);
        topPanel.add(lblPlayerStats);
        getContentPane().add(topPanel, BorderLayout.NORTH);

        teamListModel = new DefaultListModel<>();
        teamList = new JList<>(teamListModel);
        teamList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teamList.addListSelectionListener(this::onTeamSelected);

        playerListModel = new DefaultListModel<>();
        playerList = new JList<>(playerListModel);
        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playerList.addListSelectionListener(this::onPlayerSelected);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(teamList), new JScrollPane(playerList));
        split.setDividerLocation(300);

        getContentPane().setLayout(new BorderLayout(5,5));
        getContentPane().add(split, BorderLayout.CENTER);

        refreshTeamList();
    }

    /**
     * Refreshes the team list, sorting by descending points.
     */
    private void refreshTeamList() {
        teamListModel.clear();
        List<Team> sorted = manager.getTeams();
        sorted.sort(Comparator.comparingInt(Team::getPoints).reversed());
        for (Team t : sorted) {
            teamListModel.addElement(t);
        }
        lblPlayerStats.setText("Select a player to see stats");
    }

    private void onTeamSelected(ListSelectionEvent e) {
        Team team = teamList.getSelectedValue();
        playerListModel.clear();
        if (team != null) {
            lblTeamStats.setText(team.toString());
            for (Player p : team.getPlayers()) {
                playerListModel.addElement(p);
            }
        } else {
            lblTeamStats.setText("Select a team to see stats");
        }
    }

    private void onPlayerSelected(ListSelectionEvent e) {
        Player p = playerList.getSelectedValue();
        if (p != null) {
            String stats = String.format(
                    "%s: Goals: %d Assists: %d",
                    p.getName(), p.getGoals(), p.getAssists()
            );
            if ("Goalkeeper".equalsIgnoreCase(p.getPosition())) {
                stats += String.format(
                        " Saves: %d Conceded: %d",
                        p.getSaves(), p.getGoalsConceded()
                );
            }
            lblPlayerStats.setText(stats);
        } else {
            lblPlayerStats.setText("Select a player to see stats");
        }
    }

    private void addTeam() {
        String name = JOptionPane.showInputDialog(this, "Enter team name:");
        if (name != null && !name.trim().isEmpty()) {
            if (manager.findTeam(name.trim()) != null) {
                JOptionPane.showMessageDialog(this, "Team already exists.");
            } else {
                Team t = new Team(name.trim());
                manager.addTeam(t);
                refreshTeamList();
            }
        }
    }

    private void addPlayer() {
        Team t = teamList.getSelectedValue();
        if (t == null) {
            JOptionPane.showMessageDialog(this, "Select a team first.");
            return;
        }
        String pname = JOptionPane.showInputDialog(this, "Enter player name:");
        if (pname == null || pname.trim().isEmpty()) return;
        String pos = JOptionPane.showInputDialog(this, "Enter position:");
        if (pos == null || pos.trim().isEmpty()) return;
        Player p = new Player(pname.trim(), pos.trim());
        t.addPlayer(p);
        t.recalculateStats();
        refreshTeamList();
    }

    @SuppressWarnings("unchecked")
    private void recordMatch() {
        List<Team> teams = manager.getTeams();
        Team home = (Team) JOptionPane.showInputDialog(
                this, "Select home team:", "Record Match", JOptionPane.PLAIN_MESSAGE,
                null, teams.toArray(), null);
        if (home == null) return;
        Team away = (Team) JOptionPane.showInputDialog(
                this, "Select away team:", "Record Match", JOptionPane.PLAIN_MESSAGE,
                null, teams.stream().filter(t -> t != home).toArray(), null);
        if (away == null) return;

        int hg = Integer.parseInt(JOptionPane.showInputDialog(this, "Home goals:"));
        int ag = Integer.parseInt(JOptionPane.showInputDialog(this, "Away goals:"));

        for (int i = 1; i <= hg; i++) {
            Player scorer = (Player) JOptionPane.showInputDialog(
                    this, "Home scorer #" + i + ":", "Scorer",
                    JOptionPane.PLAIN_MESSAGE, null, home.getPlayers().toArray(), null);
            if (scorer != null) scorer.updateStats(1, 0, 0, 0);
            Player assister = (Player) JOptionPane.showInputDialog(
                    this, "Assister for goal #" + i + " (Cancel for none):", "Assister",
                    JOptionPane.PLAIN_MESSAGE, null, home.getPlayers().toArray(), null);
            if (assister != null && assister != scorer) assister.updateStats(0, 1, 0, 0);
        }

        for (int i = 1; i <= ag; i++) {
            Player scorer = (Player) JOptionPane.showInputDialog(
                    this, "Away scorer #" + i + ":", "Scorer",
                    JOptionPane.PLAIN_MESSAGE, null, away.getPlayers().toArray(), null);
            if (scorer != null) scorer.updateStats(1, 0, 0, 0);
            Player assister = (Player) JOptionPane.showInputDialog(
                    this, "Assister for goal #" + i + " (Cancel for none):", "Assister",
                    JOptionPane.PLAIN_MESSAGE, null, away.getPlayers().toArray(), null);
            if (assister != null && assister != scorer) assister.updateStats(0, 1, 0, 0);
        }

        for (Player p : home.getPlayers()) {
            if ("Goalkeeper".equalsIgnoreCase(p.getPosition())) {
                int sv = Integer.parseInt(JOptionPane.showInputDialog(this,
                        "Saves by " + p.getName() + ":"));
                p.updateStats(0, 0, sv, ag);
            }
        }
        for (Player p : away.getPlayers()) {
            if (
                    "Goalkeeper".equalsIgnoreCase(p.getPosition())) {
                int sv = Integer.parseInt(JOptionPane.showInputDialog(this,
                        "Saves by " + p.getName() + ":"));
                p.updateStats(0, 0, sv, hg);
            }
        }

        home.recalculateStats();
        away.recalculateStats();
        manager.recordMatchPoints(home, away, hg, ag);
        refreshTeamList();

        JOptionPane.showMessageDialog(this,
                String.format("Match recorded: %s %d - %d %s (Pts: %d vs %d)",
                        home.getName(), hg, ag, away.getName(), home.getPoints(), away.getPoints()));

        if (manager.isCompetitionOver()) {
            Team winner = manager.getWinner();
            JOptionPane.showMessageDialog(this,
                    String.format("Competition Over! Winner: %s with %d points.",
                            winner.getName(), winner.getPoints()),
                    "Competition Ended", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
