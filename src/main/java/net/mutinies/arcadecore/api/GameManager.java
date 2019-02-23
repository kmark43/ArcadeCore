package net.mutinies.arcadecore.api;

import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.map.GameMap;
import net.mutinies.arcadecore.manager.Manager;

public interface GameManager extends Manager {
    boolean isGameRunning();
    void setGame(String gameName);
    Game getGame();
    void setMap(String gameMap);
    void chooseRandomMap();
    GameMap getMap();
    
    default StartResult startGame() {
        if (isGameRunning()) {
            return StartResult.INVALID_STATE;
        }
        
        if (getGame() == null) {
            return StartResult.NO_GAME_DEFINED;
        }
        
        if (getMap() == null) {
            return StartResult.NO_MAP_DEFINED;
        }
        
        return startGame(getGame().getName(), getMap().getName());
    }
    
    default StartResult startGame(String gameName) {
        if (isGameRunning()) {
            return StartResult.INVALID_STATE;
        }
        
        if (getMap() == null) {
            return StartResult.NO_MAP_DEFINED;
        }
        
        return startGame(gameName, getMap().getName());
    }
    
    StartResult startGame(String gameName, String gameMap);
    StopResult stopGame();
    void handleGameStop();
}
