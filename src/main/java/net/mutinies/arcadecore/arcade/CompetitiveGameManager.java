package net.mutinies.arcadecore.arcade;

import net.mutinies.arcadecore.api.GameManager;
import net.mutinies.arcadecore.api.StartResult;
import net.mutinies.arcadecore.api.StopResult;
import net.mutinies.arcadecore.arcade.participation.ParticipationManager;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.map.GameMap;

public class CompetitiveGameManager implements GameManager {
    @Override
    public boolean isGameRunning() {
        return false;
    }
    
    @Override
    public void setGame(String gameName) {
    
    }
    
    @Override
    public Game getGame() {
        return null;
    }
    
    @Override
    public void setMap(String gameMap) {
    
    }
    
    @Override
    public void chooseRandomMap() {
    
    }
    
    @Override
    public GameMap getMap() {
        return null;
    }
    
    @Override
    public ParticipationManager getParticipationManager() {
        return null;
    }
    
    @Override
    public StartResult startGame(String gameName, String gameMap) {
        return null;
    }
    
    @Override
    public StopResult stopGame() {
        return null;
    }
    
    @Override
    public void handleGameStop() {
    
    }
}
