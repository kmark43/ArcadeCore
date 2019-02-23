package net.mutinies.arcadecore.item;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ClickEvent extends Event implements Cancellable {
    private static HandlerList handlerList = new HandlerList();
    
    private final Player player;
    private final ClickType clickType;
    private final InteractType interactType;
    private boolean cancelled = false;
    
    public ClickEvent(Player player, ClickType clickType, InteractType interactType) {
        this.player = Objects.requireNonNull(player);
        this.clickType = Objects.requireNonNull(clickType);
        this.interactType = Objects.requireNonNull(interactType);
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public ClickType getClickType() {
        return clickType;
    }
    
    public InteractType getInteractType() {
        return interactType;
    }
    
    public ItemStack getItem() {
        return player.getItemInHand();
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
    
    public enum ClickType {
        LEFT, RIGHT
    }
    
    public enum InteractType {
        AIR, BLOCK, ENTITY
    }
}
