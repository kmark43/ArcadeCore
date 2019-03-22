package net.mutinies.arcadecore.modules.stats;

import net.mutinies.arcadecore.event.GameDeathEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.stats.StatsManager;
import net.mutinies.arcadecore.game.stats.StatsProperty;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class KillStatsTracker implements Module {
    private Game game;
    
    public KillStatsTracker(Game game) {
        this.game = game;
        game.getStatsManager().registerProperty(new StatsProperty("kills", "Kills", true, 0));
    }
    
    @EventHandler
    public void onGameDeath(GameDeathEvent e) {
        if (!(e.getKiller() instanceof Player)) return;
    
        StatsManager statsManager = game.getStatsManager();
        Player player = (Player)e.getKiller();
        int kills = (int)statsManager.getValue(player, "kills", 0);
        statsManager.setValue(player, "kills", kills + 1);
    }
}
