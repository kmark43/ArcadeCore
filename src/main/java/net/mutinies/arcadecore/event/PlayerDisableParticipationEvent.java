package net.mutinies.arcadecore.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerDisableParticipationEvent extends PlayerEvent {
    private static HandlerList handlerList = new HandlerList();
    
    public PlayerDisableParticipationEvent(Player who) {
        super(who);
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
