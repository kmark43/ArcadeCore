package net.mutinies.arcadecore.graphics.inventory;

import net.mutinies.arcadecore.graphics.inventory.event.ClickHandler;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class WindowButton extends Observable {
    private ItemStack stack;
    private List<ClickHandler> clickHandlers;
    
    public WindowButton(ItemStack stack, ClickHandler... clickHandlers) {
        this.stack = stack;
        this.clickHandlers = clickHandlers == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(clickHandlers));
    }
    
    public void addHandler(ClickHandler handler) {
        clickHandlers.add(handler);
    }
    
    public void removeHandler(ClickHandler handler) {
        clickHandlers.remove(handler);
    }
    
    public void setStack(ItemStack stack) {
        if (this.stack != stack) {
            this.stack = stack;
            setChanged();
            notifyObservers();
            clearChanged();
        }
    }
    
    public ItemStack getStack() {
        return stack;
    }
    
    public List<ClickHandler> getClickHandlers() {
        return Collections.unmodifiableList(clickHandlers);
    }
}
