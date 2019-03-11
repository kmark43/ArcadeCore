package net.mutinies.arcadecore.games.paintball.event.territory;

import net.mutinies.arcadecore.game.team.GameTeam;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TerritoryUnclaimEvent extends Event {
    private static HandlerList handlerList = new HandlerList();
    
    private Territory territory;
    private GameTeam team;
    
    public TerritoryUnclaimEvent(Territory territory, GameTeam team) {
        this.territory = territory;
        this.team = team;
    }
    
    public Territory getTerritory() {
        return territory;
    }
    
    public GameTeam getTeam() {
        return team;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
