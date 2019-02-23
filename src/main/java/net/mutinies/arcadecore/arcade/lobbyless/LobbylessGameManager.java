package net.mutinies.arcadecore.arcade.lobbyless;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.api.GameManager;
import net.mutinies.arcadecore.api.StartResult;
import net.mutinies.arcadecore.api.StopResult;
import net.mutinies.arcadecore.arcade.ArcadeManager;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.map.GameMap;
import net.mutinies.arcadecore.game.map.MapManager;
import net.mutinies.arcadecore.manager.Manager;
import net.mutinies.arcadecore.util.PlayerSnapshot;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LobbylessGameManager implements GameManager, Manager {
    private boolean running = false;
    private Game game;
    private Map<UUID, PlayerSnapshot> snapshotMap;
    
    @Override
    public void enable() {
        snapshotMap = new HashMap<>();
        
        GameCommandExecutor gameExecutor = new GameCommandExecutor();
        PluginCommand gameCommand = ArcadeCorePlugin.getInstance().getCommand("game");
        gameCommand.setExecutor(gameExecutor);
        gameCommand.setTabCompleter(gameExecutor);
    }
    
    @Override
    public void disable() {
        if (isGameRunning()) {
            game.getGameStateManager().quickStop();
        }
    }
    
    @Override
    public boolean isGameRunning() {
        return running;
    }
    
    @Override
    public void setGame(String gameName) {
        if (gameName == null) {
            this.game = null;
        } else {
            ArcadeManager arcadeManager = ArcadeCorePlugin.getInstance().getArcadeManager();
            game = arcadeManager.getGame(gameName);
        }
    }
    
    @Override
    public void setMap(String gameMap) {
        if (game == null) {
            throw new IllegalStateException("No game defined");
        }
    
        MapManager mapManager = game.getMapManager();
        mapManager.setCurrentMap(mapManager.getMap(gameMap));
    }
    
    @Override
    public void chooseRandomMap() {
        if (game == null) {
            throw new IllegalStateException("No game defined");
        }
    
        MapManager mapManager = game.getMapManager();
        mapManager.chooseRandomMap();
    }
    
    @Override
    public StartResult startGame(String gameName, String gameMap) {
        ArcadeManager arcadeManager = ArcadeCorePlugin.getInstance().getArcadeManager();
        if (running) {
            return StartResult.INVALID_STATE;
        }
        
        if (gameName == null || arcadeManager.getGame(gameName) == null) {
            return StartResult.NO_GAME_DEFINED;
        }
        
        setGame(gameName);
    
        if (gameMap == null) {
            chooseRandomMap();
        } else if (game.getMapManager().getMap(gameMap) != null) {
            setMap(gameMap);
        }
    
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            snapshotMap.put(onlinePlayer.getUniqueId(), new PlayerSnapshot(onlinePlayer));
        }
        
        game.getGameStateManager().start();
        running = true;
        
        return StartResult.STARTED;
    }
    
    @Override
    public StopResult stopGame() {
        if (game == null || !running) {
            return StopResult.INVALID_STATE;
        }
        game.getGameStateManager().stop();
        return StopResult.STOPPED;
    }
    
    @Override
    public Game getGame() {
        return game;
    }
    
    @Override
    public GameMap getMap() {
        if (game == null) {
            return null;
        }
        return game.getMapManager().getCurrentMap();
    }
    
    @Override
    public void handleGameStop() {
        // teleport and reset players to state before game started
        // do not use bukkit scheduler, could be disabling
        for (PlayerSnapshot value : snapshotMap.values()) {
            value.restore();
        }
        snapshotMap.clear();
        running = false;
        game = null;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (snapshotMap.containsKey(e.getPlayer().getUniqueId())) {
            snapshotMap.remove(e.getPlayer().getUniqueId()).restore();
        }
    }
}
