package net.mutinies.arcadecore.games.paintball.event.territory;

import net.mutinies.arcadecore.modules.territory.Territory;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TerritoryRespawnEvent extends Event {
    private static HandlerList handlerList = new HandlerList();
    
    private Player player;
    private Territory territory;
    
    public TerritoryRespawnEvent(Player player, Territory territory) {
        this.player = player;
        this.territory = territory;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Territory getTerritory() {
        return territory;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
