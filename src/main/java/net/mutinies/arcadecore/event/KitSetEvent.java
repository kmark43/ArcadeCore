package net.mutinies.arcadecore.event;

import net.mutinies.arcadecore.game.kit.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KitSetEvent extends Event {
    private static HandlerList handlerList = new HandlerList();
    
    private Player player;
    private Kit kit;
    
    public KitSetEvent(Player player, Kit kit) {
        this.player = player;
        this.kit = kit;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Kit getKit() {
        return kit;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
