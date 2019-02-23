package net.mutinies.arcadecore.modules.prevent;

import net.mutinies.arcadecore.module.Module;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class NoRegenModule implements Module {
    @EventHandler
    public void onPlayerRegen(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        e.setCancelled(true);
    }
}
