package net.mutinies.arcadecore.games.paintball;

import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.stats.StatsManager;
import net.mutinies.arcadecore.game.stats.StatsProperty;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class RevivesStatsTracker implements Module {
    private Game game;
    
    public RevivesStatsTracker(Game game) {
        this.game = game;
        game.getStatsManager().registerProperty(new StatsProperty("revives", "Revives", true, 0));
    }
    
    @EventHandler
    public void onPlayerRevive(PotionRespawnEvent e) {
        StatsManager statsManager = game.getStatsManager();
        Player player = e.getReviver();
        int revives = (int)statsManager.getValue(player, "revives", 0);
        statsManager.setValue(player, "revives", revives + 1);
    }
}
