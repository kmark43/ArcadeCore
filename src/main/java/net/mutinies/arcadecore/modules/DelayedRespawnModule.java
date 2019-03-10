package net.mutinies.arcadecore.modules;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.event.GameDeathEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DelayedRespawnModule implements Module {
    private int delay;
    private Map<UUID, BukkitTask> respawnTasks;
    
    public DelayedRespawnModule(int delay) {
        this.delay = delay;
    }
    
    @Override
    public void enable() {
        respawnTasks = new HashMap<>();
    }
    
    @Override
    public void disable() {
        respawnTasks.values().forEach(BukkitTask::cancel);
        respawnTasks = null;
    }
    
    @EventHandler
    public void onPlayerDeath(GameDeathEvent e) {
        Game game = ArcadeCorePlugin.getGame();
    
        BukkitTask task = Bukkit.getScheduler().runTaskLater(ArcadeCorePlugin.getInstance(), () -> {
            game.getDamageManager().respawn(e.getKilled());
            respawnTasks.remove(e.getKilled().getUniqueId());
        }, delay);
        
        respawnTasks.put(e.getKilled().getUniqueId(), task);
    }
}
