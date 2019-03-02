package net.mutinies.arcadecore.event;

import net.mutinies.arcadecore.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class GameSetEvent extends Event {
    private static HandlerList handlerList = new HandlerList();
    
    private Game oldGame;
    private Game newGame;
    
    public GameSetEvent(Game oldGame, Game newGame) {
        this.oldGame = oldGame;
        this.newGame = newGame;
    }
    
    public Game getOldGame() {
        return oldGame;
    }
    
    public Game getNewGame() {
        return newGame;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
