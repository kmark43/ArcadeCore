package net.mutinies.arcadecore.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerHealthChangeEvent extends Event {
    private static HandlerList handlerList = new HandlerList();
    
    private Player player;
    private double oldHealth;
    private double newHealth;
    
    public PlayerHealthChangeEvent(Player player, double oldHealth, double newHealth) {
        this.player = player;
        this.oldHealth = oldHealth;
        this.newHealth = newHealth;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public double getOldHealth() {
        return oldHealth;
    }
    
    public double getNewHealth() {
        return newHealth;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
