package net.mutinies.arcadecore.arcade;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.manager.Manager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ArcadeManager implements Manager {
    private Map<Class<? extends Plugin>, List<String>> pluginToGameMap;
    private Map<String, Game> gameMap;
    
    public ArcadeManager() {
        pluginToGameMap = new HashMap<>();
        gameMap = new HashMap<>();
    }
    
    @EventHandler
    public void onPluginDisable(PluginDisableEvent e) {
        if (e.getPlugin().getClass() != ArcadeCorePlugin.class && pluginToGameMap.containsKey(e.getPlugin().getClass())) {
            List<String> gameNames = pluginToGameMap.remove(e.getPlugin().getClass());
            
            // todo check if game is running and stop fast
            for (String gameName : gameNames) {
                gameMap.remove(gameName);
            }
        }
    }
    
    public void registerGame(Plugin plugin, Game game) {
        if (!pluginToGameMap.containsKey(plugin.getClass())) {
            pluginToGameMap.put(plugin.getClass(), new ArrayList<>());
        }
        
        pluginToGameMap.get(plugin.getClass()).add(game.getName());
        gameMap.put(game.getName(), game);
    }
    
    public boolean hasGame(String gameName) {
        return gameMap.containsKey(gameName);
    }
    
    public Game getGame(String gameName) {
        return gameMap.get(gameName);
    }
    
    public Collection<Game> getGames() {
        return gameMap.values();
    }
}
