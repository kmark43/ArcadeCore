package net.mutinies.arcadecore.games.paintball;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PotionRespawnEvent extends Event {
    private static HandlerList handlerList = new HandlerList();
    private Player player;
    private Player reviver;
    
    public PotionRespawnEvent(Player player, Player reviver) {
        this.player = player;
        this.reviver = reviver;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Player getReviver() {
        return reviver;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
