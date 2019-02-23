package net.mutinies.arcadecore.modules.prevent;

import net.mutinies.arcadecore.module.Module;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class NoInventoryChange implements Module {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }
}
