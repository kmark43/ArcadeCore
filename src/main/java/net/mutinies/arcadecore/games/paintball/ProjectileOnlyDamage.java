package net.mutinies.arcadecore.games.paintball;

import net.mutinies.arcadecore.module.Module;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class ProjectileOnlyDamage implements Module {
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        
        if (e.getCause() != EntityDamageEvent.DamageCause.PROJECTILE && e.getCause() != EntityDamageEvent.DamageCause.VOID) {
            e.setCancelled(true);
        }
    }
}
