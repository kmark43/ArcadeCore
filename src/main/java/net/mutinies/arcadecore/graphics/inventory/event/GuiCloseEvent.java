package net.mutinies.arcadecore.graphics.inventory.event;

import net.mutinies.arcadecore.graphics.inventory.InventoryWindow;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.List;

public class GuiCloseEvent {
    private InventoryCloseEvent event;
    private InventoryWindow window;
    
    public GuiCloseEvent(InventoryCloseEvent event, InventoryWindow window) {
        this.event = event;
        this.window = window;
    }
    
    public HumanEntity getPlayer() {
        return event.getPlayer();
    }
    
    public Inventory getInventory() {
        return event.getInventory();
    }
    
    public List<HumanEntity> getViewers() {
        return event.getViewers();
    }
    
    public InventoryView getView() {
        return event.getView();
    }
    
    public InventoryWindow getWindow() {
        return window;
    }
}
