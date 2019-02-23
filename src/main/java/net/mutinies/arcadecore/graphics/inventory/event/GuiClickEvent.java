package net.mutinies.arcadecore.graphics.inventory.event;

import net.mutinies.arcadecore.graphics.inventory.InventoryWindow;
import net.mutinies.arcadecore.graphics.inventory.WindowButton;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GuiClickEvent {
    private InventoryClickEvent inventoryClickEvent;
    private InventoryWindow gui;
    private WindowButton clickedButton;
    
    public GuiClickEvent(InventoryClickEvent inventoryClickEvent, InventoryWindow gui, WindowButton clickedButton) {
        this.inventoryClickEvent = inventoryClickEvent;
        this.gui = gui;
        this.clickedButton = clickedButton;
    }
    
    public Inventory getClickedInventory() {
        return inventoryClickEvent.getClickedInventory();
    }
    
    public InventoryType.SlotType getSlotType() {
        return inventoryClickEvent.getSlotType();
    }
    
    public ItemStack getCursor() {
        return inventoryClickEvent.getCursor();
    }
    
    public ItemStack getCurrentItem() {
        return inventoryClickEvent.getCurrentItem();
    }
    
    public boolean isRightClick() {
        return inventoryClickEvent.isRightClick();
    }
    
    public boolean isLeftClick() {
        return inventoryClickEvent.isLeftClick();
    }
    
    public boolean isShiftClick() {
        return inventoryClickEvent.isShiftClick();
    }
    
    public void setCurrentItem(ItemStack stack) {
        inventoryClickEvent.setCurrentItem(stack);
    }
    
    public int getSlot() {
        return inventoryClickEvent.getSlot();
    }
    
    public int getRawSlot() {
        return inventoryClickEvent.getRawSlot();
    }
    
    public int getHotbarButton() {
        return inventoryClickEvent.getHotbarButton();
    }
    
    public InventoryAction getAction() {
        return inventoryClickEvent.getAction();
    }
    
    public ClickType getClick() {
        return inventoryClickEvent.getClick();
    }
    
    public HumanEntity getWhoClicked() {
        return inventoryClickEvent.getWhoClicked();
    }
    
    public void setResult(Event.Result newResult) {
        inventoryClickEvent.setResult(newResult);
    }
    
    public Event.Result getResult() {
        return inventoryClickEvent.getResult();
    }
    
    public Inventory getInventory() {
        return inventoryClickEvent.getInventory();
    }
    
    public List<HumanEntity> getViewers() {
        return inventoryClickEvent.getViewers();
    }
    
    public InventoryView getView() {
        return inventoryClickEvent.getView();
    }
    
    public InventoryWindow getGui() {
        return gui;
    }
    
    public WindowButton getClickedButton() {
        return clickedButton;
    }
}
