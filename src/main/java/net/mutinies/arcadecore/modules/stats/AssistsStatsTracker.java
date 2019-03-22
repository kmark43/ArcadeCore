package net.mutinies.arcadecore.modules.stats;

import net.mutinies.arcadecore.event.GameDeathEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.stats.StatsManager;
import net.mutinies.arcadecore.game.stats.StatsProperty;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class AssistsStatsTracker implements Module {
    private Game game;
    
    public AssistsStatsTracker(Game game, boolean show) {
        this.game = game;
        game.getStatsManager().registerProperty(new StatsProperty("assists", "Assists", show, 0));
    }
    
    @EventHandler
    public void onGameDeath(GameDeathEvent e) {
        StatsManager statsManager = game.getStatsManager();
        for (Player player : e.getContributingPlayers()) {
            if (e.getKiller() != null && player.getUniqueId().equals(e.getKiller().getUniqueId())) {
                continue;
            }
            int assists = (int)statsManager.getValue(player, "assists", 0);
            statsManager.setValue(player, "assists", assists + 1);
        }
    }
}
