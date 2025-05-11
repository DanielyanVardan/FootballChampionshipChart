import Core.ChampionshipManager;
import Core.Team;
import Core.Player;
import GUI.ChampionshipGUI;
import CLI.MainCLI;

import javax.swing.SwingUtilities;

/**
 * Entry point: starts CLI or GUI based on launch arguments.
 */
public class Main {
    public static void main(String[] args) {
        // 1️⃣ Initialize championship manager
        ChampionshipManager manager = new ChampionshipManager();

        // 2️⃣ Seed sample data (if desired)
        Team t1 = new Team("Real Madrid");
        t1.addPlayer(new Player("Cristiano Ronaldo", "Forward"));
        t1.addPlayer(new Player("Iker Casillas", "Goalkeeper"));
        manager.addTeam(t1);

        Team t2 = new Team("FC Barcelona");
        t2.addPlayer(new Player("Lionel Messi", "Forward"));
        t2.addPlayer(new Player("Mark Ter-Stegen", "Goalkeeper"));
        manager.addTeam(t2);

        // 3️⃣ Decide interface
        if (args.length > 0 && args[0].equalsIgnoreCase("cli")) {
            new MainCLI(manager).run();
        } else {
            SwingUtilities.invokeLater(() -> {
                new ChampionshipGUI(manager).setVisible(true);
            });
        }
    }
}
