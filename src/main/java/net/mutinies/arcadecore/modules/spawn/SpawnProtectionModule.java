package net.mutinies.arcadecore.modules.spawn;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpawnProtectionModule implements Module {
    private Set<UUID> recentlySpawnedPlayers = new HashSet<>();
    
    public SpawnProtectionModule() {
    }
    
    @Override
    public void enable() {
        recentlySpawnedPlayers = new HashSet<>();
    }
    
    @Override
    public void disable() {
        recentlySpawnedPlayers.clear();
    }
    
    public void protect(Player player, int delay) {
        recentlySpawnedPlayers.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(ArcadeCorePlugin.getInstance(), () ->
                recentlySpawnedPlayers.remove(player.getUniqueId()), delay);
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (recentlySpawnedPlayers.contains(e.getEntity().getUniqueId())) {
            e.setCancelled(true);
        }
    }
}
