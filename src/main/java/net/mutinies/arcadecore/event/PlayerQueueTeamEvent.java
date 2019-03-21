package net.mutinies.arcadecore.event;

import net.mutinies.arcadecore.game.team.GameTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerQueueTeamEvent extends Event {
    private static HandlerList handlerList = new HandlerList();
    
    private Player player;
    private GameTeam team;
    private int position;
    private int queueSize;
    
    public PlayerQueueTeamEvent(Player player, GameTeam team, int position, int queueSize) {
        this.player = player;
        this.team = team;
        this.position = position;
        this.queueSize = queueSize;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public GameTeam getTeam() {
        return team;
    }
    
    public int getPosition() {
        return position;
    }
    
    public int getQueueSize() {
        return queueSize;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
