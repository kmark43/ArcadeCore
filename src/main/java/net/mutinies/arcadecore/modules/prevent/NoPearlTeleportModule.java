package net.mutinies.arcadecore.modules.prevent;

import net.mutinies.arcadecore.module.Module;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;

public class NoPearlTeleportModule implements Module {
    @EventHandler
    public void onPearlTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            e.setCancelled(true);
        }
    }
}
