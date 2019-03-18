package net.mutinies.arcadecore.modules;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.event.GameRespawnEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.config.ConfigProperty;
import net.mutinies.arcadecore.game.config.ConfigType;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpawnProtectionModule implements Module {
    private Game game;
    private Set<UUID> recentlySpawnedPlayers = new HashSet<>();
    
    public SpawnProtectionModule(Game game, int protection) {
        this.game = game;
        game.getConfigManager().registerProperty(new ConfigProperty(ConfigType.INT, "spawn_protection", protection));
    }
    
    @EventHandler
    public void onGameRespawn(GameRespawnEvent e) {
        int cooldown = (int)game.getConfigManager().getProperty("spawn_protection").getValue();
        
        if (cooldown > 0) {
            recentlySpawnedPlayers.add(e.getPlayer().getUniqueId());
            Bukkit.getScheduler().runTaskLater(ArcadeCorePlugin.getInstance(), () ->
                    recentlySpawnedPlayers.remove(e.getPlayer().getUniqueId()), cooldown);
        }
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (recentlySpawnedPlayers.contains(e.getEntity().getUniqueId())) {
            e.setCancelled(true);
        }
    }
}
