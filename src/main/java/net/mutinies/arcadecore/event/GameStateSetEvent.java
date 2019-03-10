package net.mutinies.arcadecore.event;

import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.state.GameStateManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStateSetEvent extends Event {
    private static HandlerList handlerList = new HandlerList();
    
    private Game game;
    private GameStateManager.GameState oldState;
    private GameStateManager.GameState newState;
    
    public GameStateSetEvent(Game game, GameStateManager.GameState oldState, GameStateManager.GameState newState) {
        this.game = game;
        this.oldState = oldState;
        this.newState = newState;
    }
    
    public Game getGame() {
        return game;
    }
    
    public GameStateManager.GameState getOldState() {
        return oldState;
    }
    
    public GameStateManager.GameState getNewState() {
        return newState;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
