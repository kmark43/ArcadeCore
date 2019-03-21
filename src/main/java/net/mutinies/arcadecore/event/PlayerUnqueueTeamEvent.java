package net.mutinies.arcadecore.event;

import net.mutinies.arcadecore.game.team.GameTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerUnqueueTeamEvent extends Event {
    private static HandlerList handlerList = new HandlerList();
    
    private Player player;
    private GameTeam team;
    
    public PlayerUnqueueTeamEvent(Player player, GameTeam team) {
        this.player = player;
        this.team = team;
    }
    
    public Player getPlayer() {
        return player;
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
