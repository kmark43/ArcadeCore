package net.mutinies.arcadecore.modules.stats;

import net.mutinies.arcadecore.event.GameDeathEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.stats.StatsManager;
import net.mutinies.arcadecore.game.stats.StatsProperty;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class DeathsStatsTracker implements Module {
    private Game game;
    
    public DeathsStatsTracker(Game game, boolean show) {
        this.game = game;
        game.getStatsManager().registerProperty(new StatsProperty("deaths", "Deaths", show, 0));
    }
    
    @EventHandler
    public void onGameDeath(GameDeathEvent e) {
        StatsManager statsManager = game.getStatsManager();
        Player player = e.getKilled();
        int deaths = (int)statsManager.getValue(player, "deaths", 0);
        statsManager.setValue(player, "deaths", deaths + 1);
    }
}
