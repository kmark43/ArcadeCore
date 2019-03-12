package net.mutinies.arcadecore.event;

import net.mutinies.arcadecore.game.Game;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class GameEndCheckEvent extends Event implements Cancellable {
    private static HandlerList handlerList = new HandlerList();
    
    private boolean cancelled = false;
    private Game game;
    private CheckReason checkReason;
    
    public GameEndCheckEvent(Game game, CheckReason reason) {
        this.game = game;
        this.checkReason= reason;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public CheckReason getCheckReason() {
        return checkReason;
    }
    
    public Game getGame() {
        return game;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
    
    public enum CheckReason {
        SCORE, TOO_FEW_ALIVE
    }
}
