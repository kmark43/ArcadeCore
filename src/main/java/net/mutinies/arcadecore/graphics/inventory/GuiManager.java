package net.mutinies.arcadecore.graphics.inventory;

import net.mutinies.arcadecore.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager implements Manager {
    private Map<UUID, InventoryWindow> guiViewingMap;
    
    @Override
    public void enable() {
        guiViewingMap = new HashMap<>();
    }
    
    @Override
    public void disable() {
        for (UUID uuid : guiViewingMap.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            
            if (player != null) {
                player.closeInventory();
            }
        }
        guiViewingMap = null;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        UUID uuid = e.getWhoClicked().getUniqueId();
        if (guiViewingMap.containsKey(uuid)) {
            e.setCancelled(true);
            guiViewingMap.get(uuid).processClick(e);
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (guiViewingMap.containsKey(uuid)) {
            InventoryWindow window = guiViewingMap.get(uuid);
            window.processClose(e);
            if (window.getInventory().equals(e.getInventory())) {
                guiViewingMap.remove(uuid);
            }
        }
    }
    
    void registerGui(Player player, InventoryWindow window) {
        guiViewingMap.put(player.getUniqueId(), window);
    }
}
