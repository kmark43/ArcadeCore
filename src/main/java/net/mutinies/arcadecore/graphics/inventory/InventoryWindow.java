package net.mutinies.arcadecore.graphics.inventory;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.graphics.inventory.event.ClickHandler;
import net.mutinies.arcadecore.graphics.inventory.event.GuiClickEvent;
import net.mutinies.arcadecore.graphics.inventory.event.GuiCloseEvent;
import net.mutinies.arcadecore.graphics.inventory.event.GuiCloseHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class InventoryWindow {
    private Inventory inventory;
    private Map<Integer, WindowButton> buttonMap;
    private List<ClickHandler> fallbackHandlerList;
    private List<ClickHandler> globalClickHandlerList;
    private List<GuiCloseHandler> closeHandlerList;
    
    public InventoryWindow() {
        this(null, 6 * 9);
    }
    
    public InventoryWindow(String name) {
        this(name, 6 * 9);
    }
    
    public InventoryWindow(int size) {
        this(null, size);
    }
    
    public InventoryWindow(String name, int size) {
        if (name == null) {
            inventory = Bukkit.createInventory(null, size);
        } else {
            inventory = Bukkit.createInventory(null, size, name);
        }
        buttonMap = new HashMap<>();
    }
    
    public InventoryWindow(InventoryType type) {
        this(null, type);
    }
    
    public InventoryWindow(String name, InventoryType type) {
        if (name == null) {
            inventory = Bukkit.createInventory(null, type);
        } else {
            inventory = Bukkit.createInventory(null, type, name);
        }
        buttonMap = new HashMap<>();
    }

    public void addFallbackHandler(ClickHandler handler) {
        Objects.requireNonNull(handler);
        if (fallbackHandlerList == null) {
            fallbackHandlerList = new ArrayList<>();
        }
        fallbackHandlerList.add(handler);
    }
    
    public void removeFallbackHandler(ClickHandler handler) {
        Objects.requireNonNull(handler);
        if (fallbackHandlerList != null) {
            fallbackHandlerList.remove(handler);
            if (fallbackHandlerList.isEmpty()) {
                fallbackHandlerList = null;
            }
        }
    }
    
    public void addGlobalHandler(ClickHandler handler) {
        Objects.requireNonNull(handler);
        if (globalClickHandlerList == null) {
            globalClickHandlerList = new ArrayList<>();
        }
        globalClickHandlerList.add(handler);
    }
    
    public void removeGlobalHandler(ClickHandler handler) {
        Objects.requireNonNull(handler);
        if (globalClickHandlerList != null) {
            globalClickHandlerList.remove(handler);
            if (globalClickHandlerList.isEmpty()) {
                globalClickHandlerList = null;
            }
        }
    }
    
    public void addCloseHandler(GuiCloseHandler handler) {
        Objects.requireNonNull(handler);
        if (closeHandlerList== null) {
            closeHandlerList = new ArrayList<>();
        }
        closeHandlerList.add(handler);
    }
    
    public void removeCloseHandler(GuiCloseHandler handler) {
        Objects.requireNonNull(handler);
        if (closeHandlerList != null) {
            closeHandlerList.remove(handler);
            if (closeHandlerList.isEmpty()) {
                closeHandlerList = null;
            }
        }
    }
    
    public void set(int slot, WindowButton button) {
        if (button == null) {
            clear(slot);
        } else {
            if (buttonMap.containsKey(slot)) {
                WindowButton previousButton = buttonMap.remove(slot);
                previousButton.deleteObservers();
            }
    
            button.addObserver((observable, arg) -> inventory.setItem(slot, button.getStack()));
            buttonMap.put(slot, button);
            inventory.setItem(slot, button.getStack());
        }
    }
    
    public void clear(int slot) {
        if (buttonMap.containsKey(slot)) {
            WindowButton button = buttonMap.remove(slot);
            button.deleteObservers();
            inventory.setItem(slot, null);
        }
    }
    
    void processClick(InventoryClickEvent inventoryClickEvent) {
        Objects.requireNonNull(inventoryClickEvent);

        WindowButton clickedButton = null;
        if (buttonMap.containsKey(inventoryClickEvent.getRawSlot())) {
            clickedButton = buttonMap.get(inventoryClickEvent.getRawSlot());
        }
        
        GuiClickEvent event = new GuiClickEvent(inventoryClickEvent, this, clickedButton);
        
        if (globalClickHandlerList != null) {
            for (ClickHandler handler : globalClickHandlerList) {
                handler.processClick(event);
            }
        }
        
        if (clickedButton != null) {
            if (clickedButton.getClickHandlers() != null) {
                for (ClickHandler handler : clickedButton.getClickHandlers()) {
                    handler.processClick(event);
                }
            }
        } else if (fallbackHandlerList != null) {
            for (ClickHandler handler : fallbackHandlerList) {
                handler.processClick(event);
            }
        }
    }
    
    public void processClose(InventoryCloseEvent inventoryCloseEvent) {
        Objects.requireNonNull(inventoryCloseEvent);
        GuiCloseEvent event = new GuiCloseEvent(inventoryCloseEvent, this);
        if (closeHandlerList != null) {
            for (GuiCloseHandler handler : closeHandlerList) {
                handler.handleClosed(event);
            }
        }
    }
    
    public void show(Player player) {
        Objects.requireNonNull(player);
        player.openInventory(inventory);
        ArcadeCorePlugin.getManagerHandler().getManager(GuiManager.class).registerGui(player, this);
    }
    
    public String getInventoryTitle() {
        return inventory.getTitle();
    }
    
    public InventoryType getInventoryType() {
        return inventory.getType();
    }
    
    public int getInventorySize() {
        return inventory.getSize();
    }
    
    Inventory getInventory() {
        return inventory;
    }
}
