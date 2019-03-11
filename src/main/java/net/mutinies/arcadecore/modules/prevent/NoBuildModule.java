package net.mutinies.arcadecore.modules.prevent;

import net.mutinies.arcadecore.module.Module;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public class NoBuildModule implements Module {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }
}
