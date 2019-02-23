package net.mutinies.arcadecore.modules.prevent;

import net.mutinies.arcadecore.module.Module;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class NoDamageModule implements Module {
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!(e instanceof Player)) {
            e.setCancelled(true);
        }
    }
}
