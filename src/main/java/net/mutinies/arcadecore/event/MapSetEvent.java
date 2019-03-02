package net.mutinies.arcadecore.event;

import net.mutinies.arcadecore.game.map.GameMap;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MapSetEvent extends Event {
    private static HandlerList handlerList = new HandlerList();
    
    private GameMap map;
    
    public MapSetEvent(GameMap map) {
        this.map = map;
    }
    
    public GameMap getMap() {
        return map;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
